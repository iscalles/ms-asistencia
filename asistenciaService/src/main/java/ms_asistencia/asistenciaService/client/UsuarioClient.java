package ms_asistencia.asistenciaService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuario", url = "${ms-usuario.url}")
public interface UsuarioClient {

    @GetMapping("/usuario/interno/{idUsuario}")
    UsuarioDTOInternal obtenerUsuarioPorId(@PathVariable("idUsuario") Long idUsuario);
}
