package ms_asistencia.asistenciaService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "periodo_escolar")
public class PeriodoEscolar {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "periodo_escolar_seq")
    @SequenceGenerator(name = "periodo_escolar_seq", sequenceName = "seq_periodo_escolar", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaInicio;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaFin;

    public PeriodoEscolar() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
