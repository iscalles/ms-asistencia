package ms_asistencia.asistenciaService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-academico", url = "${ms-academico.url}")
public interface AcademicoClient {

    @GetMapping("/matriculas/{id}")
    MatriculaDTOInternal obtenerMatriculaPorId(@PathVariable("id") Long id);

    @GetMapping("/matriculas/curso/{idCurso}")
    List<MatriculaDTOInternal> listarMatriculasPorCurso(@PathVariable("idCurso") Long idCurso);
}
