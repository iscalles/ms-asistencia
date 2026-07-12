package ms_asistencia.asistenciaService.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import ms_asistencia.asistenciaService.model.FechaExcluida;
import ms_asistencia.asistenciaService.repository.FechaExcluidaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fechas-excluidas")
public class FechaExcluidaController {

    private final FechaExcluidaRepository repository;

    public FechaExcluidaController(FechaExcluidaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<FechaExcluida> listar() {
        return repository.findAllByOrderByFechaAsc();
    }

    @PostMapping
    public FechaExcluida agregar(@RequestBody FechaExcluida fechaExcluida) {
        return repository.save(fechaExcluida);
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
