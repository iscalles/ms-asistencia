package ms_asistencia.asistenciaService.services.impl;

import ms_asistencia.asistenciaService.client.AcademicoClient;
import ms_asistencia.asistenciaService.client.MatriculaDTOInternal;
import ms_asistencia.asistenciaService.dto.AsistenciaLoteRequestDTO;
import ms_asistencia.asistenciaService.dto.DetalleAsistenciaDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaDiaDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaResumenDTO;
import ms_asistencia.asistenciaService.model.Asistencia;
import ms_asistencia.asistenciaService.repository.AsistenciaRepository;
import ms_asistencia.asistenciaService.services.AsistenciaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final AsistenciaRepository asistenciaRepository;
    private final AcademicoClient academicoClient;

    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                 AcademicoClient academicoClient) {
        this.asistenciaRepository = asistenciaRepository;
        this.academicoClient = academicoClient;
    }

    // Normaliza el estado a "Primera mayúscula, resto minúscula" (ej: "AUSENTE" -> "Ausente")
    // independientemente de cómo llegue desde el cliente, para mantener consistencia en la BD.
    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) return estado;
        String limpio = estado.trim();
        return limpio.substring(0, 1).toUpperCase() + limpio.substring(1).toLowerCase();
    }

    @Override
    public List<Asistencia> listarAsistencias() {
        return asistenciaRepository.findAll();
    }

    @Override
    public Asistencia buscarAsistenciaPorId(Long id) {
        return asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));
    }

    @Override
    public List<Asistencia> buscarAsistenciaPorEstado(String estado) {
        return asistenciaRepository.findAllByEstadoAsistencia(normalizarEstado(estado));
    }

    @Override
    public List<Asistencia> buscarHistorialPorMatricula(Long idMatricula) {
        return asistenciaRepository.findAllByIdMatricula(idMatricula);
    }

    @Override
    public List<MatriculaDTOInternal> obtenerRosterCurso(Long idCurso) {
        return academicoClient.listarMatriculasPorCurso(idCurso);
    }

    @Override
    @Transactional
    public List<Asistencia> registrarAsistenciaLote(AsistenciaLoteRequestDTO request) {
        List<MatriculaDTOInternal> roster = academicoClient.listarMatriculasPorCurso(request.getIdCurso());
        Set<Long> matriculasValidas = roster.stream()
                .map(MatriculaDTOInternal::getIdMatricula)
                .collect(Collectors.toSet());

        List<Asistencia> registros = new ArrayList<>();
        for (DetalleAsistenciaDTO detalle : request.getDetalles()) {
            if (!matriculasValidas.contains(detalle.getIdMatricula())) {
                throw new RuntimeException("La matrícula " + detalle.getIdMatricula() + " no pertenece al curso " + request.getIdCurso());
            }
            Asistencia asistencia = new Asistencia();
            asistencia.setIdMatricula(detalle.getIdMatricula());
            asistencia.setFechaAsistencia(request.getFechaAsistencia());
            asistencia.setEstadoAsistencia(normalizarEstado(detalle.getEstadoAsistencia()));
            asistencia.setJustificacionAsistencia(detalle.getJustificacionAsistencia());
            registros.add(asistencia);
        }

        // Si ya existía asistencia tomada para este curso/fecha (ej: el docente guarda dos veces),
        // se reemplaza en vez de duplicar registros.
        asistenciaRepository.deleteAllByIdMatriculaInAndFechaAsistencia(matriculasValidas, request.getFechaAsistencia());
        return asistenciaRepository.saveAll(registros);
    }

    @Override
    public Asistencia crearAsistencia(Asistencia asistencia) {
        // Valida que la matrícula existe en ms-academico antes de guardar
        academicoClient.obtenerMatriculaPorId(asistencia.getIdMatricula());
        asistencia.setEstadoAsistencia(normalizarEstado(asistencia.getEstadoAsistencia()));
        return asistenciaRepository.save(asistencia);
    }

    @Override
    public Asistencia actualizarAsistencia(Long id, Asistencia asistencia) {
        Asistencia existente = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));

        // Valida que la nueva matrícula existe en ms-academico
        academicoClient.obtenerMatriculaPorId(asistencia.getIdMatricula());

        existente.setFechaAsistencia(asistencia.getFechaAsistencia());
        existente.setJustificacionAsistencia(asistencia.getJustificacionAsistencia());
        existente.setEstadoAsistencia(normalizarEstado(asistencia.getEstadoAsistencia()));
        existente.setIdMatricula(asistencia.getIdMatricula());
        return asistenciaRepository.save(existente);
    }

    @Override
    public void eliminarAsistencia(Long id) {
        Asistencia existente = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));
        asistenciaRepository.delete(existente);
    }

    @Override
    public List<ReporteAsistenciaDiaDTO> reporteAsistenciaPorCursoYFecha(Long idCurso, LocalDate fecha) {
        List<MatriculaDTOInternal> roster = academicoClient.listarMatriculasPorCurso(idCurso);
        Set<Long> idsMatricula = roster.stream().map(MatriculaDTOInternal::getIdMatricula).collect(Collectors.toSet());

        // merge function: si hay registros duplicados para la misma matrícula/fecha (datos de prueba
        // anteriores a la corrección del lote), se queda con el más reciente en vez de fallar.
        Map<Long, Asistencia> registrosPorMatricula = asistenciaRepository
                .findAllByIdMatriculaInAndFechaAsistencia(idsMatricula, fecha).stream()
                .collect(Collectors.toMap(Asistencia::getIdMatricula, a -> a,
                        (a1, a2) -> a1.getId_asistencia() > a2.getId_asistencia() ? a1 : a2));

        return roster.stream()
                .map(alumno -> {
                    Asistencia registro = registrosPorMatricula.get(alumno.getIdMatricula());
                    return new ReporteAsistenciaDiaDTO(
                            alumno.getIdMatricula(),
                            alumno.getNombreEstudiante(),
                            alumno.getRutEstudiante(),
                            registro != null ? registro.getEstadoAsistencia() : "sin registro",
                            registro != null ? registro.getJustificacionAsistencia() : null
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public ReporteAsistenciaResumenDTO reporteResumenPorCurso(Long idCurso, LocalDate desde, LocalDate hasta) {
        List<MatriculaDTOInternal> roster = academicoClient.listarMatriculasPorCurso(idCurso);
        Set<Long> idsMatricula = roster.stream().map(MatriculaDTOInternal::getIdMatricula).collect(Collectors.toSet());

        List<Asistencia> registros = asistenciaRepository
                .findAllByIdMatriculaInAndFechaAsistenciaBetween(idsMatricula, desde, hasta);

        ReporteAsistenciaResumenDTO dto = new ReporteAsistenciaResumenDTO();
        dto.setIdCurso(idCurso);
        dto.setDesde(desde.format(FORMATO_FECHA));
        dto.setHasta(hasta.format(FORMATO_FECHA));
        dto.setTotalRegistros(registros.size());
        dto.setTotalPresentes(registros.stream().filter(a -> "presente".equalsIgnoreCase(a.getEstadoAsistencia())).count());
        dto.setTotalAusentes(registros.stream().filter(a -> "ausente".equalsIgnoreCase(a.getEstadoAsistencia())).count());
        dto.setTotalJustificados(registros.stream().filter(a -> "justificado".equalsIgnoreCase(a.getEstadoAsistencia())).count());
        dto.setPorcentajeAsistencia(dto.getTotalRegistros() == 0
                ? 0
                : (dto.getTotalPresentes() * 100.0) / dto.getTotalRegistros());
        return dto;
    }
}
