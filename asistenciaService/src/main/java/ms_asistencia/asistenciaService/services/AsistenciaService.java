package ms_asistencia.asistenciaService.services;

import ms_asistencia.asistenciaService.client.MatriculaDTOInternal;
import ms_asistencia.asistenciaService.dto.AsistenciaLoteRequestDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaDiaDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaResumenDTO;
import ms_asistencia.asistenciaService.dto.ValidacionFechaDTO;
import ms_asistencia.asistenciaService.model.Asistencia;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaService {
    List<Asistencia> listarAsistencias();
    Asistencia buscarAsistenciaPorId(Long id);
    List<Asistencia> buscarAsistenciaPorEstado(String Estado);
    List<Asistencia> buscarHistorialPorMatricula(Long idMatricula);
    List<MatriculaDTOInternal> obtenerRosterCurso(Long idCurso);
    Asistencia crearAsistencia(Asistencia asistencia);
    Asistencia actualizarAsistencia(Long id, Asistencia asistencia);
    List<Asistencia> registrarAsistenciaLote(AsistenciaLoteRequestDTO request);
    void eliminarAsistencia(Long id);

    // ── Validación de fechas ────────────────────────────────────────────────────
    ValidacionFechaDTO validarFechaAsistencia(LocalDate fecha);
    /** Valida la fecha + verifica que haya clase para el CursoAsignatura indicado ese día. */
    ValidacionFechaDTO validarFechaAsistencia(LocalDate fecha, Long idCursoAsignatura);

    // ── Reportes administrativos ────────────────────────────────────────────────
    List<ReporteAsistenciaDiaDTO> reporteAsistenciaPorCursoYFecha(Long idCurso, LocalDate fecha);
    ReporteAsistenciaResumenDTO reporteResumenPorCurso(Long idCurso, LocalDate desde, LocalDate hasta);
}
