package ms_asistencia.asistenciaservice.services;

import ms_asistencia.asistenciaservice.model.Asistencia;

import java.util.List;

public interface AsistenciaService {
    List<Asistencia> listarAsistencias();
    Asistencia buscarAsistenciaPorId(Long id);
    List<Asistencia> buscarAsistenciaPorEstado(String Estado);
    Asistencia crearAsistencia(Asistencia asistencia);
    Asistencia actualizarAsistencia(Long id, Asistencia asistencia);
    void eliminarAsistencia(Long id);
}
