package ms_asistencia.asistenciaService.services;

import ms_asistencia.asistenciaService.model.Asistencia;

import java.util.List;

public interface AsistenciaService {
    List<Asistencia> listarAsistencias();
    Asistencia buscarAsistenciaPorId(Long id);
    List<Asistencia> buscarAsistenciaPorEstado(String Estado);
    Asistencia crearAsistencia(Asistencia asistencia);
    Asistencia actualizarAsistencia(Long id, Asistencia asistencia);
    void eliminarAsistencia(Long id);
}
