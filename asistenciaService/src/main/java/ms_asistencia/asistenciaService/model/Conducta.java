package ms_asistencia.asistenciaService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
public class Conducta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conducta_seq")
    @SequenceGenerator(name = "conducta_seq", sequenceName = "seq_conducta", allocationSize = 1)
    private Long id_conducta;
    @Column(name = "tipo_conducta")
    private String tipoConducta;
    @Column(name = "descripcion_conducta")
    private String descripcionConducta;
    @Column(name = "fecha_conducta")
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate fechaConducta;
    @Column(name = "DOCENTE_ID_USUARIO", nullable = false)
    private Long docenteIdUsuario;
    @Column(name = "ESTUDIANTE_ID_USUARIO")
    private Long estudianteIdUsuario;

    public Conducta() {

    }

    public Conducta(Long id_conducta, String tipoConducta, String descripcionConducta, LocalDate fechaConducta, Long docenteIdUsuario, Long estudianteIdUsuario) {
        this.id_conducta = id_conducta;
        this.tipoConducta = tipoConducta;
        this.descripcionConducta = descripcionConducta;
        this.fechaConducta = fechaConducta;
        this.docenteIdUsuario = docenteIdUsuario;
        this.estudianteIdUsuario = estudianteIdUsuario;
    }

    public Long getId_conducta() {
        return id_conducta;
    }

    public void setId_conducta(Long id_conducta) {
        this.id_conducta = id_conducta;
    }

    public String getTipoConducta() {
        return tipoConducta;
    }

    public void setTipoConducta(String tipoConducta) {
        this.tipoConducta = tipoConducta;
    }

    public String getDescripcionConducta() {
        return descripcionConducta;
    }

    public void setDescripcionConducta(String descripcionConducta) {
        this.descripcionConducta = descripcionConducta;
    }

    public LocalDate getFechaConducta() {
        return fechaConducta;
    }

    public void setFechaConducta(LocalDate fechaConducta) {
        this.fechaConducta = fechaConducta;
    }

    public Long getDocenteIdUsuario() {
        return docenteIdUsuario;
    }

    public void setDocenteIdUsuario(Long docenteIdUsuario) {
        this.docenteIdUsuario = this.docenteIdUsuario;
    }

    public Long getEstudianteIdUsuario() {
        return estudianteIdUsuario;
    }

    public void setEstudianteIdUsuario(Long estudianteIdUsuario) {
        this.estudianteIdUsuario = estudianteIdUsuario;
    }
}
