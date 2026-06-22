package ms_asistencia.asistenciaService.services;

import ms_asistencia.asistenciaService.dto.ReporteConductaAlumnoDTO;
import ms_asistencia.asistenciaService.dto.ConductaResponseDTO;
import ms_asistencia.asistenciaService.model.Conducta;

import java.util.List;

public interface ConductaService {
    List<Conducta> listarConductas();
    Conducta buscarConductaPorId(Long id);
    List<Conducta> buscarConductaPorTipo(String tipo);
    List<Conducta> buscarHistorialPorEstudiante(Long estudianteIdUsuario);
    List<ConductaResponseDTO> buscarHistorialPorEstudianteDTO(Long estudianteIdUsuario);
    Conducta crearConducta(Conducta conducta);
    Conducta actualizarConducta(Long id, Conducta conducta);
    void eliminarConducta(Long id);

    // ── Reportes administrativos ────────────────────────────────────────────────
    List<ReporteConductaAlumnoDTO> reporteResumenPorCurso(Long idCurso);
}
