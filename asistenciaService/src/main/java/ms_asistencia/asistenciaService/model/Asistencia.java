package ms_asistencia.asistenciaService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;


import java.time.LocalDate;


@Entity
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asistencia_seq")
    @SequenceGenerator(name = "asistencia_seq", sequenceName = "seq_asistencia", allocationSize = 1)
    private Long id_asistencia;
    @Column(name = "fecha_asistencia")
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate fechaAsistencia;
    @Column(name = "justificacion_asistencia")
    private String justificacionAsistencia;
    @Column(name = "estado_asistencia")
    private String estadoAsistencia;
    @Column(name = "matricula_id_matricula")
    private Long idMatricula;

    public Asistencia() {

    }
    public Asistencia(Long id_asistencia, LocalDate fechaAsistencia, String justificacionAsistencia, String estadoAsistencia, Long idMatricula) {
        this.id_asistencia = id_asistencia;
        this.fechaAsistencia = fechaAsistencia;
        this.justificacionAsistencia = justificacionAsistencia;
        this.estadoAsistencia = estadoAsistencia;
        this.idMatricula = idMatricula;
    }

    public Long getId_asistencia() {
        return id_asistencia;
    }

    public void setId_asistencia(Long id_asistencia) {
        this.id_asistencia = id_asistencia;
    }

    public LocalDate getFechaAsistencia() {
        return fechaAsistencia;
    }

    public void setFechaAsistencia(LocalDate fechaAsistencia) {
        this.fechaAsistencia = fechaAsistencia;
    }

    public String getJustificacionAsistencia() {
        return justificacionAsistencia;
    }

    public void setJustificacionAsistencia(String justificacionAsistencia) {
        this.justificacionAsistencia = justificacionAsistencia;
    }

    public String getEstadoAsistencia() {
        return estadoAsistencia;
    }

    public void setEstadoAsistencia(String estadoAsistencia) {
        this.estadoAsistencia = estadoAsistencia;
    }

    public Long getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
    }
}
