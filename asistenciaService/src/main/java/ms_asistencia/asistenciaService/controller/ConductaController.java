package ms_asistencia.asistenciaService.controller;

import ms_asistencia.asistenciaService.model.Conducta;
import ms_asistencia.asistenciaService.services.ConductaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conducta")
public class ConductaController {
    private final ConductaService service;

    public ConductaController(ConductaService service) {
        this.service = service;
    }

    @GetMapping()
    List<Conducta> listarConductas() {
        return service.listarConductas();
    }

    @GetMapping("/{id}")
    Conducta buscarConductaPorId(@PathVariable Long id) {
        return service.buscarConductaPorId(id);
    }

    @GetMapping("/tipo/{tipo}")
    List<Conducta> buscarConductaPorTipo(@PathVariable String tipo){
        return service.buscarConductaPorTipo(tipo);
    }

    @PostMapping()
    Conducta crearConducta(@RequestBody Conducta conducta){
        return service.crearConducta(conducta);
    }

    @PutMapping("/{id}")
    Conducta actualizarConducta(@PathVariable Long id,@RequestBody Conducta conducta){
        return service.actualizarConducta(id, conducta);
    }

    @DeleteMapping("/{id}")
    void eliminarConducta(@PathVariable Long id){
        service.eliminarConducta(id);
    }
}
