package ms_asistencia.asistenciaService.controller;

import ms_asistencia.asistenciaService.model.Asistencia;
import ms_asistencia.asistenciaService.services.AsistenciaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asistencia")
public class AsistenciaController {
    private final AsistenciaService service;

    public AsistenciaController(AsistenciaService service) {
        this.service = service;
    }

    @GetMapping()
    List<Asistencia> listarAsistencias() {
        return service.listarAsistencias();
    }

    @GetMapping("/{id}")
    Asistencia buscarAsistenciaPorId(@PathVariable Long id) {
        return service.buscarAsistenciaPorId(id);
    }

    @GetMapping("/estado/{estado}")
    List<Asistencia> buscarAsistenciaPorEstado(@PathVariable String estado){
        return service.buscarAsistenciaPorEstado(estado);
    }

    @PostMapping()
    Asistencia crearAsistencia(@RequestBody Asistencia asistencia) {
        return service.crearAsistencia(asistencia);
    }

    @PutMapping("/{id}")
    Asistencia actualizarAsistencia(@PathVariable Long id,@RequestBody Asistencia asistencia){
        return service.actualizarAsistencia(id, asistencia);
    }

    @DeleteMapping("/{id}")
    void eliminarAsistencia(@PathVariable Long id){
        service.eliminarAsistencia(id);
    }

}
