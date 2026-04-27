package ms_asistencia.asistenciaservice.repository;

import ms_asistencia.asistenciaservice.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findAllByEstadoAsistencia(String estadoAsistencia);
}
