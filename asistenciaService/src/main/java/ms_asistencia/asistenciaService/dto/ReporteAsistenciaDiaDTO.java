package ms_asistencia.asistenciaService.dto;

public class ReporteAsistenciaDiaDTO {

    private Long idMatricula;
    private String nombreEstudiante;
    private String rutEstudiante;
    private String estadoAsistencia;
    private String justificacionAsistencia;

    public ReporteAsistenciaDiaDTO() {}

    public ReporteAsistenciaDiaDTO(Long idMatricula, String nombreEstudiante, String rutEstudiante,
                                    String estadoAsistencia, String justificacionAsistencia) {
        this.idMatricula = idMatricula;
        this.nombreEstudiante = nombreEstudiante;
        this.rutEstudiante = rutEstudiante;
        this.estadoAsistencia = estadoAsistencia;
        this.justificacionAsistencia = justificacionAsistencia;
    }

    public Long getIdMatricula() { return idMatricula; }
    public void setIdMatricula(Long idMatricula) { this.idMatricula = idMatricula; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getRutEstudiante() { return rutEstudiante; }
    public void setRutEstudiante(String rutEstudiante) { this.rutEstudiante = rutEstudiante; }

    public String getEstadoAsistencia() { return estadoAsistencia; }
    public void setEstadoAsistencia(String estadoAsistencia) { this.estadoAsistencia = estadoAsistencia; }

    public String getJustificacionAsistencia() { return justificacionAsistencia; }
    public void setJustificacionAsistencia(String justificacionAsistencia) { this.justificacionAsistencia = justificacionAsistencia; }
}
