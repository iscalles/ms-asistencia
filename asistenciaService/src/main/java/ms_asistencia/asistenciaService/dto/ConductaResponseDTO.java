package ms_asistencia.asistenciaService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class ConductaResponseDTO {

    private Long idConducta;
    private String tipoConducta;
    private String descripcionConducta;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaConducta;

    private Long docenteIdUsuario;
    private String nombreDocente;

    private Long estudianteIdUsuario;

    public ConductaResponseDTO() {}

    public Long getIdConducta() { return idConducta; }
    public void setIdConducta(Long idConducta) { this.idConducta = idConducta; }

    public String getTipoConducta() { return tipoConducta; }
    public void setTipoConducta(String tipoConducta) { this.tipoConducta = tipoConducta; }

    public String getDescripcionConducta() { return descripcionConducta; }
    public void setDescripcionConducta(String descripcionConducta) { this.descripcionConducta = descripcionConducta; }

    public LocalDate getFechaConducta() { return fechaConducta; }
    public void setFechaConducta(LocalDate fechaConducta) { this.fechaConducta = fechaConducta; }

    public Long getDocenteIdUsuario() { return docenteIdUsuario; }
    public void setDocenteIdUsuario(Long docenteIdUsuario) { this.docenteIdUsuario = docenteIdUsuario; }

    public String getNombreDocente() { return nombreDocente; }
    public void setNombreDocente(String nombreDocente) { this.nombreDocente = nombreDocente; }

    public Long getEstudianteIdUsuario() { return estudianteIdUsuario; }
    public void setEstudianteIdUsuario(Long estudianteIdUsuario) { this.estudianteIdUsuario = estudianteIdUsuario; }
}
