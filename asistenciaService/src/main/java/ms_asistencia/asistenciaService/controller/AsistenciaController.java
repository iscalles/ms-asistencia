package ms_asistencia.asistenciaService.controller;

import ms_asistencia.asistenciaService.client.MatriculaDTOInternal;
import ms_asistencia.asistenciaService.dto.AsistenciaLoteRequestDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaDiaDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaResumenDTO;
import ms_asistencia.asistenciaService.dto.ValidacionFechaDTO;
import ms_asistencia.asistenciaService.model.Asistencia;
import ms_asistencia.asistenciaService.services.AsistenciaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/matricula/{idMatricula}")
    List<Asistencia> buscarHistorialPorMatricula(@PathVariable Long idMatricula) {
        return service.buscarHistorialPorMatricula(idMatricula);
    }

    @GetMapping("/curso/{idCurso}/roster")
    List<MatriculaDTOInternal> obtenerRosterCurso(@PathVariable Long idCurso) {
        return service.obtenerRosterCurso(idCurso);
    }

    @GetMapping("/curso/{idCurso}/reporte-dia")
    List<ReporteAsistenciaDiaDTO> reporteAsistenciaPorCursoYFecha(
            @PathVariable Long idCurso,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {
        return service.reporteAsistenciaPorCursoYFecha(idCurso, fecha);
    }

    @GetMapping("/curso/{idCurso}/reporte-resumen")
    ReporteAsistenciaResumenDTO reporteResumenPorCurso(
            @PathVariable Long idCurso,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate desde,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate hasta) {
        return service.reporteResumenPorCurso(idCurso, desde, hasta);
    }

    @GetMapping("/validar-fecha")
    ValidacionFechaDTO validarFecha(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {
        return service.validarFechaAsistencia(fecha);
    }

    @PostMapping()
    Asistencia crearAsistencia(@RequestBody Asistencia asistencia) {
        return service.crearAsistencia(asistencia);
    }

    @PostMapping("/lote")
    List<Asistencia> registrarAsistenciaLote(@RequestBody AsistenciaLoteRequestDTO request) {
        return service.registrarAsistenciaLote(request);
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
