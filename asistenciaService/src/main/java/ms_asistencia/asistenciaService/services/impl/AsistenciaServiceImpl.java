package ms_asistencia.asistenciaService.services.impl;

import ms_asistencia.asistenciaService.model.Asistencia;
import ms_asistencia.asistenciaService.repository.AsistenciaRepository;
import ms_asistencia.asistenciaService.services.AsistenciaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {
    private final AsistenciaRepository asistenciaRepository;

    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository) {
        this.asistenciaRepository = asistenciaRepository;
    }


    @Override
    public List<Asistencia> listarAsistencias() {
        return asistenciaRepository.findAll();
    }

    @Override
    public Asistencia buscarAsistenciaPorId(Long id) {
        return asistenciaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Asistencia> buscarAsistenciaPorEstado(String estado) {
        return asistenciaRepository.findAllByEstadoAsistencia(estado);
    }

    @Override
    public Asistencia crearAsistencia(Asistencia asistencia) {
        return asistenciaRepository.save(asistencia);
    }

    @Override
    public Asistencia actualizarAsistencia(Long id, Asistencia asistencia) {
        Asistencia asistenciaExistente = asistenciaRepository.findById(id).orElse(null);
        if (asistenciaExistente != null) {
            asistenciaExistente.setFechaAsistencia(asistencia.getFechaAsistencia());
            asistenciaExistente.setJustificacionAsistencia(asistencia.getJustificacionAsistencia());
            asistenciaExistente.setEstadoAsistencia(asistencia.getEstadoAsistencia());
            asistenciaExistente.setIdMatricula(asistencia.getIdMatricula());
            return asistenciaRepository.save(asistenciaExistente);
        } else {
            throw new RuntimeException("Asistencia no encontrada con id: " + id);
        }
    }

    @Override
    public void eliminarAsistencia(Long id) {
        Asistencia asistenciaExistente = asistenciaRepository.findById(id).orElse(null);
        if (asistenciaExistente != null) {
            asistenciaRepository.delete(asistenciaExistente);
        } else {
            throw new RuntimeException("Asistencia no encontrada con id: " + id);
        }
    }
}
