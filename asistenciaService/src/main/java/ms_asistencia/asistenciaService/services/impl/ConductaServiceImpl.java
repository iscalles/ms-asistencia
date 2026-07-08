package ms_asistencia.asistenciaService.services.impl;

import ms_asistencia.asistenciaService.client.AcademicoClient;
import ms_asistencia.asistenciaService.client.MatriculaDTOInternal;
import ms_asistencia.asistenciaService.client.NotificacionClient;
import ms_asistencia.asistenciaService.client.NotificacionEventoRequest;
import ms_asistencia.asistenciaService.client.UsuarioClient;
import ms_asistencia.asistenciaService.client.UsuarioDTOInternal;
import ms_asistencia.asistenciaService.dto.ConductaResponseDTO;
import ms_asistencia.asistenciaService.dto.ReporteConductaAlumnoDTO;
import ms_asistencia.asistenciaService.model.Conducta;
import ms_asistencia.asistenciaService.repository.ConductaRepository;
import ms_asistencia.asistenciaService.services.ConductaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class ConductaServiceImpl implements ConductaService {

    private static final Logger log = LoggerFactory.getLogger(ConductaServiceImpl.class);

    private final ConductaRepository conductaRepository;
    private final UsuarioClient usuarioClient;
    private final AcademicoClient academicoClient;
    private final NotificacionClient notificacionClient;

    public ConductaServiceImpl(ConductaRepository conductaRepository,
                               UsuarioClient usuarioClient,
                               AcademicoClient academicoClient,
                               NotificacionClient notificacionClient) {
        this.conductaRepository = conductaRepository;
        this.usuarioClient = usuarioClient;
        this.academicoClient = academicoClient;
        this.notificacionClient = notificacionClient;
    }

    private void validarUsuarios(Long docenteId, Long estudianteId) {
        usuarioClient.obtenerUsuarioPorId(docenteId);
        usuarioClient.obtenerUsuarioPorId(estudianteId);
    }

    @Override
    public List<Conducta> listarConductas() {
        return conductaRepository.findAll();
    }

    @Override
    public Conducta buscarConductaPorId(Long id) {
        return conductaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conducta no encontrada con id: " + id));
    }

    @Override
    public List<Conducta> buscarConductaPorTipo(String tipo) {
        return conductaRepository.findAllByTipoConducta(tipo);
    }

    @Override
    public List<Conducta> buscarHistorialPorEstudiante(Long estudianteIdUsuario) {
        return conductaRepository.findAllByEstudianteIdUsuario(estudianteIdUsuario);
    }

    @Override
    public List<ConductaResponseDTO> buscarHistorialPorEstudianteDTO(Long estudianteIdUsuario) {
        return conductaRepository.findAllByEstudianteIdUsuario(estudianteIdUsuario)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private ConductaResponseDTO toResponseDTO(Conducta c) {
        ConductaResponseDTO dto = new ConductaResponseDTO();
        dto.setIdConducta(c.getId_conducta());
        dto.setTipoConducta(c.getTipoConducta());
        dto.setDescripcionConducta(c.getDescripcionConducta());
        dto.setFechaConducta(c.getFechaConducta());
        dto.setDocenteIdUsuario(c.getDocenteIdUsuario());
        dto.setEstudianteIdUsuario(c.getEstudianteIdUsuario());
        try {
            UsuarioDTOInternal docente = usuarioClient.obtenerUsuarioPorId(c.getDocenteIdUsuario());
            dto.setNombreDocente(docente.getNombreCompleto());
        } catch (Exception e) {
            dto.setNombreDocente("Docente no disponible");
        }
        return dto;
    }

    @Override
    public Conducta crearConducta(Conducta conducta) {
        validarUsuarios(conducta.getDocenteIdUsuario(), conducta.getEstudianteIdUsuario());
        Conducta guardada = conductaRepository.save(conducta);
        notificarConductaRegistrada(guardada);
        return guardada;
    }

    // Best-effort: si ms-notificacion no responde, no debe impedir que la conducta se guarde.
    private void notificarConductaRegistrada(Conducta conducta) {
        try {
            // Conducta no guarda referencia a curso; se resuelve aparte (y de forma best-effort,
            // si ms-academico no responde o el estudiante no tiene matricula, se notifica sin sección).
            String seccion = null;
            try {
                MatriculaDTOInternal matricula = academicoClient.obtenerMatriculaActualDelEstudiante(conducta.getEstudianteIdUsuario());
                if (matricula != null && matricula.getGradoCurso() != null) {
                    seccion = matricula.getGradoCurso() + " " + matricula.getSeccionCurso();
                }
            } catch (Exception e) {
                log.warn("No se pudo resolver la sección del estudiante {} para la conducta {}: {}",
                        conducta.getEstudianteIdUsuario(), conducta.getId_conducta(), e.getMessage());
            }

            NotificacionEventoRequest evento = new NotificacionEventoRequest();
            evento.setTipoNotificacion("CONDUCTA");
            evento.setEstudianteIdUsuario(conducta.getEstudianteIdUsuario());
            evento.setIdReferencia(conducta.getId_conducta());
            evento.setAsunto("Nueva anotación de conducta");
            evento.setMensaje(conducta.getTipoConducta() + ": " + conducta.getDescripcionConducta()
                    + (seccion != null ? " (" + seccion + ")" : ""));
            evento.setNotificarEstudiante(false);
            evento.setNotificarApoderados(true);
            notificacionClient.crearEvento(evento);
        } catch (Exception e) {
            log.warn("No se pudo notificar la conducta {}: {}", conducta.getId_conducta(), e.getMessage());
        }
    }

    @Override
    public Conducta actualizarConducta(Long id, Conducta conducta) {
        Conducta existente = conductaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conducta no encontrada con id: " + id));

        validarUsuarios(conducta.getDocenteIdUsuario(), conducta.getEstudianteIdUsuario());

        existente.setTipoConducta(conducta.getTipoConducta());
        existente.setDescripcionConducta(conducta.getDescripcionConducta());
        existente.setFechaConducta(conducta.getFechaConducta());
        existente.setDocenteIdUsuario(conducta.getDocenteIdUsuario());
        existente.setEstudianteIdUsuario(conducta.getEstudianteIdUsuario());
        return conductaRepository.save(existente);
    }

    @Override
    public void eliminarConducta(Long id) {
        Conducta existente = conductaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conducta no encontrada con id: " + id));
        conductaRepository.delete(existente);
    }

    @Override
    public List<ReporteConductaAlumnoDTO> reporteResumenPorCurso(Long idCurso) {
        List<MatriculaDTOInternal> roster = academicoClient.listarMatriculasPorCurso(idCurso);
        List<Long> idsEstudiante = roster.stream().map(MatriculaDTOInternal::getEstudianteIdUsuario).collect(Collectors.toList());

        Map<Long, List<Conducta>> conductasPorEstudiante = conductaRepository
                .findAllByEstudianteIdUsuarioIn(idsEstudiante).stream()
                .collect(Collectors.groupingBy(Conducta::getEstudianteIdUsuario));

        return roster.stream()
                .map(alumno -> {
                    List<Conducta> conductas = conductasPorEstudiante.getOrDefault(alumno.getEstudianteIdUsuario(), List.of());
                    ReporteConductaAlumnoDTO dto = new ReporteConductaAlumnoDTO();
                    dto.setEstudianteIdUsuario(alumno.getEstudianteIdUsuario());
                    dto.setNombreEstudiante(alumno.getNombreEstudiante());
                    dto.setRutEstudiante(alumno.getRutEstudiante());
                    dto.setTotalPositivas(conductas.stream().filter(c -> "positiva".equalsIgnoreCase(c.getTipoConducta())).count());
                    dto.setTotalNegativas(conductas.stream().filter(c -> "negativa".equalsIgnoreCase(c.getTipoConducta())).count());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
