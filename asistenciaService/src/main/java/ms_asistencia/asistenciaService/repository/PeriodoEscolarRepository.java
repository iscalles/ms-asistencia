package ms_asistencia.asistenciaService.repository;

import ms_asistencia.asistenciaService.model.PeriodoEscolar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoEscolarRepository extends JpaRepository<PeriodoEscolar, Long> {
    List<PeriodoEscolar> findAllByOrderByFechaInicioDesc();

    @Query("SELECT p FROM PeriodoEscolar p WHERE p.fechaInicio <= :fecha AND p.fechaFin >= :fecha")
    Optional<PeriodoEscolar> findPeriodoCubreFecha(@Param("fecha") LocalDate fecha);
}
