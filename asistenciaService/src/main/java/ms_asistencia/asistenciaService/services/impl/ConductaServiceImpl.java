package ms_asistencia.asistenciaService.services.impl;

import ms_asistencia.asistenciaService.client.UsuarioClient;
import ms_asistencia.asistenciaService.client.UsuarioDTOInternal;
import ms_asistencia.asistenciaService.dto.ConductaResponseDTO;
import ms_asistencia.asistenciaService.model.Conducta;
import ms_asistencia.asistenciaService.repository.ConductaRepository;
import ms_asistencia.asistenciaService.services.ConductaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
