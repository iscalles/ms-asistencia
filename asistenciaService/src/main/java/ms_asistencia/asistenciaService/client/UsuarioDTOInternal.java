package ms_asistencia.asistenciaService.client;

import java.util.Set;

public class UsuarioDTOInternal {

    private Long idUsuario;
    private String rutUsuario;
    private String nombreUsuario;
    private String primerApellidoUsuario;
    private String segundoApellidoUsuario;
    private String correoUsuario;
    private Set<String> roles;

    public UsuarioDTOInternal() {}

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getRutUsuario() { return rutUsuario; }
    public void setRutUsuario(String rutUsuario) { this.rutUsuario = rutUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getPrimerApellidoUsuario() { return primerApellidoUsuario; }
    public void setPrimerApellidoUsuario(String primerApellidoUsuario) { this.primerApellidoUsuario = primerApellidoUsuario; }

    public String getSegundoApellidoUsuario() { return segundoApellidoUsuario; }
    public void setSegundoApellidoUsuario(String segundoApellidoUsuario) { this.segundoApellidoUsuario = segundoApellidoUsuario; }

    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public String getNombreCompleto() {
        return nombreUsuario + " " + primerApellidoUsuario + " " + segundoApellidoUsuario;
    }
}
