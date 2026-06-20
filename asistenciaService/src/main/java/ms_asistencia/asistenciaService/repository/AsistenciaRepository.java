package ms_asistencia.asistenciaService.repository;

import ms_asistencia.asistenciaService.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findAllByEstadoAsistencia(String estadoAsistencia);
    List<Asistencia> findAllByIdMatricula(Long idMatricula);
    List<Asistencia> findAllByIdMatriculaInAndFechaAsistencia(Collection<Long> idMatriculas, LocalDate fechaAsistencia);
    List<Asistencia> findAllByIdMatriculaInAndFechaAsistenciaBetween(Collection<Long> idMatriculas, LocalDate desde, LocalDate hasta);
    void deleteAllByIdMatriculaInAndFechaAsistencia(Collection<Long> idMatriculas, LocalDate fechaAsistencia);
}
