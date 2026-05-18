package ms_asistencia.asistenciaService.services.impl;

import ms_asistencia.asistenciaService.client.UsuarioClient;
import ms_asistencia.asistenciaService.model.Conducta;
import ms_asistencia.asistenciaService.repository.ConductaRepository;
import ms_asistencia.asistenciaService.services.ConductaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConductaServiceImpl implements ConductaService {

    private final ConductaRepository conductaRepository;
    private final UsuarioClient usuarioClient;

    public ConductaServiceImpl(ConductaRepository conductaRepository,
                               UsuarioClient usuarioClient) {
        this.conductaRepository = conductaRepository;
        this.usuarioClient = usuarioClient;
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
    public Conducta crearConducta(Conducta conducta) {
        validarUsuarios(conducta.getDocenteIdUsuario(), conducta.getEstudianteIdUsuario());
        return conductaRepository.save(conducta);
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
}
