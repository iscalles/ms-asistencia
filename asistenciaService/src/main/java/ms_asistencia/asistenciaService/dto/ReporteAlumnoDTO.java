package ms_asistencia.asistenciaService.dto;

public class ReporteAlumnoDTO {

    private Long idMatricula;
    private Long estudianteIdUsuario;
    private String nombreEstudiante;
    private String rutEstudiante;
    private long totalPresentes;
    private long totalAusentes;
    private long totalJustificados;
    private double porcentajeAsistencia;

    public ReporteAlumnoDTO() {}

    public Long getIdMatricula() { return idMatricula; }
    public void setIdMatricula(Long idMatricula) { this.idMatricula = idMatricula; }

    public Long getEstudianteIdUsuario() { return estudianteIdUsuario; }
    public void setEstudianteIdUsuario(Long estudianteIdUsuario) { this.estudianteIdUsuario = estudianteIdUsuario; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getRutEstudiante() { return rutEstudiante; }
    public void setRutEstudiante(String rutEstudiante) { this.rutEstudiante = rutEstudiante; }

    public long getTotalPresentes() { return totalPresentes; }
    public void setTotalPresentes(long totalPresentes) { this.totalPresentes = totalPresentes; }

    public long getTotalAusentes() { return totalAusentes; }
    public void setTotalAusentes(long totalAusentes) { this.totalAusentes = totalAusentes; }

    public long getTotalJustificados() { return totalJustificados; }
    public void setTotalJustificados(long totalJustificados) { this.totalJustificados = totalJustificados; }

    public double getPorcentajeAsistencia() { return porcentajeAsistencia; }
    public void setPorcentajeAsistencia(double porcentajeAsistencia) { this.porcentajeAsistencia = porcentajeAsistencia; }
}
