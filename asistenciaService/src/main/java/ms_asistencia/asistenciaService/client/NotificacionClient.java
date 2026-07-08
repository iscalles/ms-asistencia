package ms_asistencia.asistenciaService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-notificacion", url = "${ms-notificacion.url}")
public interface NotificacionClient {

    @PostMapping("/notificaciones/evento")
    void crearEvento(@RequestBody NotificacionEventoRequest evento);
}
