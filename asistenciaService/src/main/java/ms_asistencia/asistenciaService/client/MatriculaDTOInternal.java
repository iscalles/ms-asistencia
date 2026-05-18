package ms_asistencia.asistenciaService.client;

public class MatriculaDTOInternal {

    private Long idMatricula;
    private Long estudianteIdUsuario;
    private Long anioAcademicoMatricula;

    public MatriculaDTOInternal() {}

    public Long getIdMatricula() { return idMatricula; }
    public void setIdMatricula(Long idMatricula) { this.idMatricula = idMatricula; }

    public Long getEstudianteIdUsuario() { return estudianteIdUsuario; }
    public void setEstudianteIdUsuario(Long estudianteIdUsuario) { this.estudianteIdUsuario = estudianteIdUsuario; }

    public Long getAnioAcademicoMatricula() { return anioAcademicoMatricula; }
    public void setAnioAcademicoMatricula(Long anioAcademicoMatricula) { this.anioAcademicoMatricula = anioAcademicoMatricula; }
}
