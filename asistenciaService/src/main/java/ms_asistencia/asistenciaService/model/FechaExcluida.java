package ms_asistencia.asistenciaService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "fecha_excluida")
public class FechaExcluida {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fecha_excluida_seq")
    @SequenceGenerator(name = "fecha_excluida_seq", sequenceName = "seq_fecha_excluida", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha;

    @Column(nullable = false)
    private String descripcion;

    public FechaExcluida() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
