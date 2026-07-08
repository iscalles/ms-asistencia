package ms_asistencia.asistenciaService.client;

public class MatriculaDTOInternal {

    private Long idMatricula;
    private Long estudianteIdUsuario;
    private Long anioAcademicoMatricula;
    private String nombreEstudiante;
    private String rutEstudiante;
    private Long idCurso;
    private String gradoCurso;
    private String seccionCurso;

    public MatriculaDTOInternal() {}

    public Long getIdMatricula() { return idMatricula; }
    public void setIdMatricula(Long idMatricula) { this.idMatricula = idMatricula; }

    public Long getEstudianteIdUsuario() { return estudianteIdUsuario; }
    public void setEstudianteIdUsuario(Long estudianteIdUsuario) { this.estudianteIdUsuario = estudianteIdUsuario; }

    public Long getAnioAcademicoMatricula() { return anioAcademicoMatricula; }
    public void setAnioAcademicoMatricula(Long anioAcademicoMatricula) { this.anioAcademicoMatricula = anioAcademicoMatricula; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getRutEstudiante() { return rutEstudiante; }
    public void setRutEstudiante(String rutEstudiante) { this.rutEstudiante = rutEstudiante; }

    public Long getIdCurso() { return idCurso; }
    public void setIdCurso(Long idCurso) { this.idCurso = idCurso; }

    public String getGradoCurso() { return gradoCurso; }
    public void setGradoCurso(String gradoCurso) { this.gradoCurso = gradoCurso; }

    public String getSeccionCurso() { return seccionCurso; }
    public void setSeccionCurso(String seccionCurso) { this.seccionCurso = seccionCurso; }
}
