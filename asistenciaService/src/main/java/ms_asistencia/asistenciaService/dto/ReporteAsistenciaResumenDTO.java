package ms_asistencia.asistenciaService.dto;

public class ReporteAsistenciaResumenDTO {

    private Long idCurso;
    private String desde;
    private String hasta;
    private long totalRegistros;
    private long totalPresentes;
    private long totalAusentes;
    private long totalJustificados;
    private double porcentajeAsistencia;

    public ReporteAsistenciaResumenDTO() {}

    public Long getIdCurso() { return idCurso; }
    public void setIdCurso(Long idCurso) { this.idCurso = idCurso; }

    public String getDesde() { return desde; }
    public void setDesde(String desde) { this.desde = desde; }

    public String getHasta() { return hasta; }
    public void setHasta(String hasta) { this.hasta = hasta; }

    public long getTotalRegistros() { return totalRegistros; }
    public void setTotalRegistros(long totalRegistros) { this.totalRegistros = totalRegistros; }

    public long getTotalPresentes() { return totalPresentes; }
    public void setTotalPresentes(long totalPresentes) { this.totalPresentes = totalPresentes; }

    public long getTotalAusentes() { return totalAusentes; }
    public void setTotalAusentes(long totalAusentes) { this.totalAusentes = totalAusentes; }

    public long getTotalJustificados() { return totalJustificados; }
    public void setTotalJustificados(long totalJustificados) { this.totalJustificados = totalJustificados; }

    public double getPorcentajeAsistencia() { return porcentajeAsistencia; }
    public void setPorcentajeAsistencia(double porcentajeAsistencia) { this.porcentajeAsistencia = porcentajeAsistencia; }
}
