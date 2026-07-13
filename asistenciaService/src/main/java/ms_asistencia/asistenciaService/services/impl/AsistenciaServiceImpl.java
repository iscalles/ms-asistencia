package ms_asistencia.asistenciaService.services.impl;

import ms_asistencia.asistenciaService.client.AcademicoClient;
import ms_asistencia.asistenciaService.client.MatriculaDTOInternal;
import ms_asistencia.asistenciaService.client.NotificacionClient;
import ms_asistencia.asistenciaService.client.NotificacionEventoRequest;
import ms_asistencia.asistenciaService.dto.AsistenciaLoteRequestDTO;
import ms_asistencia.asistenciaService.dto.DetalleAsistenciaDTO;
import ms_asistencia.asistenciaService.dto.ReporteAlumnoDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaDiaDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaResumenDTO;
import ms_asistencia.asistenciaService.dto.ValidacionFechaDTO;
import ms_asistencia.asistenciaService.model.Asistencia;
import ms_asistencia.asistenciaService.repository.AsistenciaRepository;
import ms_asistencia.asistenciaService.repository.FechaExcluidaRepository;
import ms_asistencia.asistenciaService.repository.PeriodoEscolarRepository;
import ms_asistencia.asistenciaService.services.AsistenciaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaServiceImpl.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Set<String> ESTADOS_NOTIFICABLES = Set.of("ausente", "justificado");

    private final AsistenciaRepository asistenciaRepository;
    private final FechaExcluidaRepository fechaExcluidaRepository;
    private final PeriodoEscolarRepository periodoEscolarRepository;
    private final AcademicoClient academicoClient;
    private final NotificacionClient notificacionClient;

    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                 FechaExcluidaRepository fechaExcluidaRepository,
                                 PeriodoEscolarRepository periodoEscolarRepository,
                                 AcademicoClient academicoClient,
                                 NotificacionClient notificacionClient) {
        this.asistenciaRepository = asistenciaRepository;
        this.fechaExcluidaRepository = fechaExcluidaRepository;
        this.periodoEscolarRepository = periodoEscolarRepository;
        this.academicoClient = academicoClient;
        this.notificacionClient = notificacionClient;
    }

    // Best-effort: si ms-notificacion no responde, no debe impedir que la asistencia se guarde.
    private void notificarSiCorresponde(Asistencia asistencia, MatriculaDTOInternal matricula) {
        if (matricula == null || !ESTADOS_NOTIFICABLES.contains(
                asistencia.getEstadoAsistencia() == null ? "" : asistencia.getEstadoAsistencia().toLowerCase())) {
            return;
        }
        try {
            String curso = matricula.getGradoCurso() != null
                    ? matricula.getGradoCurso() + " " + matricula.getSeccionCurso()
                    : null;

            NotificacionEventoRequest evento = new NotificacionEventoRequest();
            evento.setTipoNotificacion("ASISTENCIA");
            evento.setEstudianteIdUsuario(matricula.getEstudianteIdUsuario());
            evento.setIdReferencia(asistencia.getId_asistencia());
            evento.setAsunto("Registro de asistencia: " + asistencia.getEstadoAsistencia());
            evento.setMensaje("Se registró el estado \"" + asistencia.getEstadoAsistencia() + "\" el "
                    + asistencia.getFechaAsistencia().format(FORMATO_FECHA)
                    + (curso != null ? " en " + curso : "")
                    + (asistencia.getJustificacionAsistencia() != null
                        ? " (" + asistencia.getJustificacionAsistencia() + ")" : ""));
            evento.setNotificarEstudiante(false);
            evento.setNotificarApoderados(true);
            notificacionClient.crearEvento(evento);
        } catch (Exception e) {
            log.warn("No se pudo notificar la asistencia {}: {}", asistencia.getId_asistencia(), e.getMessage());
        }
    }

    @Override
    public ValidacionFechaDTO validarFechaAsistencia(LocalDate fecha) {
        return validarFechaAsistencia(fecha, null);
    }

    @Override
    public ValidacionFechaDTO validarFechaAsistencia(LocalDate fecha, Long idCursoAsignatura) {
        DayOfWeek dia = fecha.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            return new ValidacionFechaDTO(false, "La fecha corresponde a un fin de semana.");
        }
        ValidacionFechaDTO resultadoFecha = fechaExcluidaRepository.findByFecha(fecha)
                .map(fe -> new ValidacionFechaDTO(false, "Día no lectivo: " + fe.getDescripcion()))
                .orElseGet(() -> {
                    if (periodoEscolarRepository.count() > 0
                            && periodoEscolarRepository.findPeriodoCubreFecha(fecha).isEmpty()) {
                        return new ValidacionFechaDTO(false, "La fecha está fuera del período escolar configurado.");
                    }
                    return new ValidacionFechaDTO(true, null);
                });

        if (!resultadoFecha.isValida() || idCursoAsignatura == null) {
            return resultadoFecha;
        }

        // Verificar que haya clase programada ese día para este CursoAsignatura (best-effort).
        // Si no hay horario configurado (lista vacía), se permite registrar asistencia.
        try {
            List<String> diasClase = academicoClient.getDiasHorario(idCursoAsignatura);
            if (!diasClase.isEmpty() && !diasClase.contains(dia.name())) {
                return new ValidacionFechaDTO(false,
                        "No hay clases de esta asignatura el " + nombreDia(dia) + ".");
            }
        } catch (Exception e) {
            log.warn("No se pudo verificar horario para cursoAsignatura {}: {}", idCursoAsignatura, e.getMessage());
        }

        return new ValidacionFechaDTO(true, null);
    }

    private String nombreDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY    -> "lunes";
            case TUESDAY   -> "martes";
            case WEDNESDAY -> "miércoles";
            case THURSDAY  -> "jueves";
            case FRIDAY    -> "viernes";
            default        -> dia.name().toLowerCase();
        };
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
        ValidacionFechaDTO validacion = validarFechaAsistencia(request.getFechaAsistencia());
        if (!validacion.isValida()) {
            throw new RuntimeException(validacion.getMotivo());
        }

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
        List<Asistencia> guardadas = asistenciaRepository.saveAll(registros);

        Map<Long, MatriculaDTOInternal> matriculaPorId = roster.stream()
                .collect(Collectors.toMap(MatriculaDTOInternal::getIdMatricula, m -> m, (a, b) -> a, HashMap::new));
        for (Asistencia guardada : guardadas) {
            notificarSiCorresponde(guardada, matriculaPorId.get(guardada.getIdMatricula()));
        }

        return guardadas;
    }

    @Override
    public Asistencia crearAsistencia(Asistencia asistencia) {
        // Valida que la matrícula existe en ms-academico antes de guardar (y aprovechamos
        // la respuesta para saber a qué estudiante notificar)
        MatriculaDTOInternal matricula = academicoClient.obtenerMatriculaPorId(asistencia.getIdMatricula());
        asistencia.setEstadoAsistencia(normalizarEstado(asistencia.getEstadoAsistencia()));
        Asistencia guardada = asistenciaRepository.save(asistencia);
        notificarSiCorresponde(guardada, matricula);
        return guardada;
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
    public List<ReporteAlumnoDTO> reporteAsistenciaPorAlumno(Long idCurso, LocalDate desde, LocalDate hasta) {
        List<MatriculaDTOInternal> roster = academicoClient.listarMatriculasPorCurso(idCurso);
        Set<Long> idsMatricula = roster.stream().map(MatriculaDTOInternal::getIdMatricula).collect(Collectors.toSet());

        List<Asistencia> registros = asistenciaRepository
                .findAllByIdMatriculaInAndFechaAsistenciaBetween(idsMatricula, desde, hasta);

        Map<Long, List<Asistencia>> porMatricula = registros.stream()
                .collect(Collectors.groupingBy(Asistencia::getIdMatricula));

        return roster.stream().map(alumno -> {
            List<Asistencia> asistAlumno = porMatricula.getOrDefault(alumno.getIdMatricula(), List.of());
            long presentes   = asistAlumno.stream().filter(a -> "Presente".equalsIgnoreCase(a.getEstadoAsistencia())).count();
            long ausentes    = asistAlumno.stream().filter(a -> "Ausente".equalsIgnoreCase(a.getEstadoAsistencia())).count();
            long justificados = asistAlumno.stream().filter(a -> "Justificado".equalsIgnoreCase(a.getEstadoAsistencia())).count();
            long total = presentes + ausentes + justificados;

            ReporteAlumnoDTO dto = new ReporteAlumnoDTO();
            dto.setIdMatricula(alumno.getIdMatricula());
            dto.setEstudianteIdUsuario(alumno.getEstudianteIdUsuario());
            dto.setNombreEstudiante(alumno.getNombreEstudiante());
            dto.setRutEstudiante(alumno.getRutEstudiante());
            dto.setTotalPresentes(presentes);
            dto.setTotalAusentes(ausentes);
            dto.setTotalJustificados(justificados);
            dto.setPorcentajeAsistencia(total > 0 ? (presentes * 100.0) / total : 0.0);
            return dto;
        }).collect(Collectors.toList());
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
