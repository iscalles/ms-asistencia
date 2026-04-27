package ms_asistencia.asistenciaService.services.impl;

import ms_asistencia.asistenciaService.model.Conducta;
import ms_asistencia.asistenciaService.repository.ConductaRepository;
import ms_asistencia.asistenciaService.services.ConductaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConductaServiceImpl implements ConductaService {
    private final ConductaRepository conductaRepository;

    public ConductaServiceImpl(ConductaRepository conductaRepository) {
        this.conductaRepository = conductaRepository;
    }

    @Override
    public List<Conducta> listarConductas() {
        return conductaRepository.findAll();
    }

    @Override
    public Conducta buscarConductaPorId(Long id) {
        return conductaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Conducta> buscarConductaPorTipo(String tipo) {
        return conductaRepository.findAllByTipoConducta(tipo);
    }

    @Override
    public Conducta crearConducta(Conducta conducta) {
        return conductaRepository.save(conducta);
    }

    @Override
    public Conducta actualizarConducta(Long id, Conducta conducta) {
        Conducta conductaExistente = conductaRepository.findById(id).orElse(null);
        if (conductaExistente != null) {
            conductaExistente.setTipoConducta(conducta.getTipoConducta());
            conductaExistente.setDescripcionConducta(conducta.getDescripcionConducta());
            conductaExistente.setFechaConducta(conducta.getFechaConducta());
            conductaExistente.setRutDocente(conducta.getRutDocente());
            conductaExistente.setRutEstudiante(conducta.getRutEstudiante());
            return conductaRepository.save(conductaExistente);
        } else {
            throw new RuntimeException("Conducta no encontrada con id: " + id);
        }
    }

    @Override
    public void eliminarConducta(Long id) {
        Conducta conductaExistente = conductaRepository.findById(id).orElse(null);
        if (conductaExistente != null) {
            conductaRepository.delete(conductaExistente);
        } else {
            throw new RuntimeException("Conducta no encontrada con id: " + id);
        }
    }
}
