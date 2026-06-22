package ms_asistencia.asistenciaService.dto;

public class ReporteConductaAlumnoDTO {

    private Long estudianteIdUsuario;
    private String nombreEstudiante;
    private String rutEstudiante;
    private long totalPositivas;
    private long totalNegativas;

    public ReporteConductaAlumnoDTO() {}

    public Long getEstudianteIdUsuario() { return estudianteIdUsuario; }
    public void setEstudianteIdUsuario(Long estudianteIdUsuario) { this.estudianteIdUsuario = estudianteIdUsuario; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getRutEstudiante() { return rutEstudiante; }
    public void setRutEstudiante(String rutEstudiante) { this.rutEstudiante = rutEstudiante; }

    public long getTotalPositivas() { return totalPositivas; }
    public void setTotalPositivas(long totalPositivas) { this.totalPositivas = totalPositivas; }

    public long getTotalNegativas() { return totalNegativas; }
    public void setTotalNegativas(long totalNegativas) { this.totalNegativas = totalNegativas; }
}
