package ms_asistencia.asistenciaService.dto;

public class ValidacionFechaDTO {
    private boolean valida;
    private String motivo;

    public ValidacionFechaDTO() {}

    public ValidacionFechaDTO(boolean valida, String motivo) {
        this.valida = valida;
        this.motivo = motivo;
    }

    public boolean isValida() { return valida; }
    public void setValida(boolean valida) { this.valida = valida; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
