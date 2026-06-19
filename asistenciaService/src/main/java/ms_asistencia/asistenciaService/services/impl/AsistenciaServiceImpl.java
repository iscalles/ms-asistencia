package ms_asistencia.asistenciaService.services.impl;

import ms_asistencia.asistenciaService.client.AcademicoClient;
import ms_asistencia.asistenciaService.client.MatriculaDTOInternal;
import ms_asistencia.asistenciaService.dto.AsistenciaLoteRequestDTO;
import ms_asistencia.asistenciaService.dto.DetalleAsistenciaDTO;
import ms_asistencia.asistenciaService.model.Asistencia;
import ms_asistencia.asistenciaService.repository.AsistenciaRepository;
import ms_asistencia.asistenciaService.services.AsistenciaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final AcademicoClient academicoClient;

    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                 AcademicoClient academicoClient) {
        this.asistenciaRepository = asistenciaRepository;
        this.academicoClient = academicoClient;
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
        return asistenciaRepository.findAllByEstadoAsistencia(estado);
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
            asistencia.setEstadoAsistencia(detalle.getEstadoAsistencia());
            asistencia.setJustificacionAsistencia(detalle.getJustificacionAsistencia());
            registros.add(asistencia);
        }
        return asistenciaRepository.saveAll(registros);
    }

    @Override
    public Asistencia crearAsistencia(Asistencia asistencia) {
        // Valida que la matrícula existe en ms-academico antes de guardar
        academicoClient.obtenerMatriculaPorId(asistencia.getIdMatricula());
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
        existente.setEstadoAsistencia(asistencia.getEstadoAsistencia());
        existente.setIdMatricula(asistencia.getIdMatricula());
        return asistenciaRepository.save(existente);
    }

    @Override
    public void eliminarAsistencia(Long id) {
        Asistencia existente = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));
        asistenciaRepository.delete(existente);
    }
}
