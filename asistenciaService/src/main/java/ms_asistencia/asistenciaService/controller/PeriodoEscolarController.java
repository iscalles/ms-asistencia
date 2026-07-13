package ms_asistencia.asistenciaService.controller;

import ms_asistencia.asistenciaService.model.PeriodoEscolar;
import ms_asistencia.asistenciaService.repository.PeriodoEscolarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/periodos-escolares")
public class PeriodoEscolarController {

    private final PeriodoEscolarRepository repository;

    public PeriodoEscolarController(PeriodoEscolarRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PeriodoEscolar> listar() {
        return repository.findAllByOrderByFechaInicioDesc();
    }

    @PostMapping
    public PeriodoEscolar agregar(@RequestBody PeriodoEscolar periodo) {
        return repository.save(periodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
