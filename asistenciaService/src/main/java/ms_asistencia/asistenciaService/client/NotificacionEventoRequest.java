package ms_asistencia.asistenciaService.client;

public class NotificacionEventoRequest {

    private String tipoNotificacion;
    private Long estudianteIdUsuario;
    private Long idReferencia;
    private String asunto;
    private String mensaje;
    private boolean notificarEstudiante = true;
    private boolean notificarApoderados = true;

    public NotificacionEventoRequest() {
    }

    public String getTipoNotificacion() {
        return tipoNotificacion;
    }

    public void setTipoNotificacion(String tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }

    public Long getEstudianteIdUsuario() {
        return estudianteIdUsuario;
    }

    public void setEstudianteIdUsuario(Long estudianteIdUsuario) {
        this.estudianteIdUsuario = estudianteIdUsuario;
    }

    public Long getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(Long idReferencia) {
        this.idReferencia = idReferencia;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isNotificarEstudiante() {
        return notificarEstudiante;
    }

    public void setNotificarEstudiante(boolean notificarEstudiante) {
        this.notificarEstudiante = notificarEstudiante;
    }

    public boolean isNotificarApoderados() {
        return notificarApoderados;
    }

    public void setNotificarApoderados(boolean notificarApoderados) {
        this.notificarApoderados = notificarApoderados;
    }
}
