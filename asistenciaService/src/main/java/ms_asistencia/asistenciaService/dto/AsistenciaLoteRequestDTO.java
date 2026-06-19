package ms_asistencia.asistenciaService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public class AsistenciaLoteRequestDTO {

    private Long idCurso;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaAsistencia;

    private List<DetalleAsistenciaDTO> detalles;

    public AsistenciaLoteRequestDTO() {}

    public Long getIdCurso() { return idCurso; }
    public void setIdCurso(Long idCurso) { this.idCurso = idCurso; }

    public LocalDate getFechaAsistencia() { return fechaAsistencia; }
    public void setFechaAsistencia(LocalDate fechaAsistencia) { this.fechaAsistencia = fechaAsistencia; }

    public List<DetalleAsistenciaDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleAsistenciaDTO> detalles) { this.detalles = detalles; }
}
