package ms_asistencia.asistenciaService.dto;

public class DetalleAsistenciaDTO {

    private Long idMatricula;
    private String estadoAsistencia;
    private String justificacionAsistencia;

    public DetalleAsistenciaDTO() {}

    public Long getIdMatricula() { return idMatricula; }
    public void setIdMatricula(Long idMatricula) { this.idMatricula = idMatricula; }

    public String getEstadoAsistencia() { return estadoAsistencia; }
    public void setEstadoAsistencia(String estadoAsistencia) { this.estadoAsistencia = estadoAsistencia; }

    public String getJustificacionAsistencia() { return justificacionAsistencia; }
    public void setJustificacionAsistencia(String justificacionAsistencia) { this.justificacionAsistencia = justificacionAsistencia; }
}
