package ms_asistencia.asistenciaService.repository;

import ms_asistencia.asistenciaService.model.FechaExcluida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FechaExcluidaRepository extends JpaRepository<FechaExcluida, Long> {
    Optional<FechaExcluida> findByFecha(LocalDate fecha);
    List<FechaExcluida> findAllByOrderByFechaAsc();
}
