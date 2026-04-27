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
    @Column(name = "docente_usuario_rut_usuario")
    private String rutDocente;
    @Column(name = "estudiante_usuario_rut_usuario")
    private String rutEstudiante;

    public Conducta() {

    }

    public Conducta(Long id_conducta, String tipoConducta, String descripcionConducta, LocalDate fechaConducta, String rutDocente, String rutEstudiante) {
        this.id_conducta = id_conducta;
        this.tipoConducta = tipoConducta;
        this.descripcionConducta = descripcionConducta;
        this.fechaConducta = fechaConducta;
        this.rutDocente = rutDocente;
        this.rutEstudiante = rutEstudiante;
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

    public String getRutDocente() {
        return rutDocente;
    }

    public void setRutDocente(String rutDocente) {
        this.rutDocente = rutDocente;
    }

    public String getRutEstudiante() {
        return rutEstudiante;
    }

    public void setRutEstudiante(String rutEstudiante) {
        this.rutEstudiante = rutEstudiante;
    }
}
