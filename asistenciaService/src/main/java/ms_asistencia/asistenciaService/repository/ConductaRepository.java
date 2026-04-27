package ms_asistencia.asistenciaService.repository;

import ms_asistencia.asistenciaService.model.Conducta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConductaRepository extends JpaRepository<Conducta, Long> {
    List<Conducta> findAllByTipoConducta(String tipoConducta);
}
