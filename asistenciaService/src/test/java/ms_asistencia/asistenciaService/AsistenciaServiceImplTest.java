package ms_asistencia.asistenciaService;

import ms_asistencia.asistenciaService.client.AcademicoClient;
import ms_asistencia.asistenciaService.client.MatriculaDTOInternal;
import ms_asistencia.asistenciaService.client.NotificacionClient;
import ms_asistencia.asistenciaService.dto.AsistenciaLoteRequestDTO;
import ms_asistencia.asistenciaService.dto.DetalleAsistenciaDTO;
import ms_asistencia.asistenciaService.dto.ReporteAlumnoDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaDiaDTO;
import ms_asistencia.asistenciaService.dto.ReporteAsistenciaResumenDTO;
import ms_asistencia.asistenciaService.dto.ValidacionFechaDTO;
import ms_asistencia.asistenciaService.model.Asistencia;
import ms_asistencia.asistenciaService.repository.AsistenciaRepository;
import ms_asistencia.asistenciaService.repository.FechaExcluidaRepository;
import ms_asistencia.asistenciaService.repository.PeriodoEscolarRepository;
import ms_asistencia.asistenciaService.services.impl.AsistenciaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceImplTest {

    @Mock private AsistenciaRepository asistenciaRepository;
    @Mock private FechaExcluidaRepository fechaExcluidaRepository;
    @Mock private PeriodoEscolarRepository periodoEscolarRepository;
    @Mock private AcademicoClient academicoClient;
    @Mock private NotificacionClient notificacionClient;

    private AsistenciaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AsistenciaServiceImpl(
                asistenciaRepository, fechaExcluidaRepository,
                periodoEscolarRepository, academicoClient, notificacionClient);
    }

    // ── validarFechaAsistencia ────────────────────────────────────────────────

    @Test
    void validarFecha_sabado_retornaInvalida() {
        LocalDate sabado = LocalDate.of(2026, 7, 11); // sábado
        ValidacionFechaDTO resultado = service.validarFechaAsistencia(sabado);
        assertThat(resultado.isValida()).isFalse();
        assertThat(resultado.getMotivo()).contains("fin de semana");
    }

    @Test
    void validarFecha_domingo_retornaInvalida() {
        LocalDate domingo = LocalDate.of(2026, 7, 12); // domingo
        ValidacionFechaDTO resultado = service.validarFechaAsistencia(domingo);
        assertThat(resultado.isValida()).isFalse();
    }

    @Test
    void validarFecha_lunesHabil_retornaValida() {
        LocalDate lunes = LocalDate.of(2026, 7, 6); // lunes
        when(fechaExcluidaRepository.findByFecha(lunes)).thenReturn(Optional.empty());
        when(periodoEscolarRepository.count()).thenReturn(0L);

        ValidacionFechaDTO resultado = service.validarFechaAsistencia(lunes);
        assertThat(resultado.isValida()).isTrue();
    }

    // ── buscarAsistenciaPorId ─────────────────────────────────────────────────

    @Test
    void buscarPorId_existente_retornaAsistencia() {
        Asistencia a = new Asistencia(1L, LocalDate.now(), null, "Presente", 10L);
        when(asistenciaRepository.findById(1L)).thenReturn(Optional.of(a));

        Asistencia resultado = service.buscarAsistenciaPorId(1L);
        assertThat(resultado.getId_asistencia()).isEqualTo(1L);
        assertThat(resultado.getEstadoAsistencia()).isEqualTo("Presente");
    }

    @Test
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(asistenciaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarAsistenciaPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ── eliminarAsistencia ────────────────────────────────────────────────────

    @Test
    void eliminarAsistencia_existente_invocaDelete() {
        Asistencia a = new Asistencia(1L, LocalDate.now(), null, "Presente", 10L);
        when(asistenciaRepository.findById(1L)).thenReturn(Optional.of(a));

        service.eliminarAsistencia(1L);
        verify(asistenciaRepository).delete(a);
    }

    @Test
    void eliminarAsistencia_noExiste_lanzaExcepcion() {
        when(asistenciaRepository.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.eliminarAsistencia(5L))
                .isInstanceOf(RuntimeException.class);
    }

    // ── buscarAsistenciaPorEstado normaliza el estado ─────────────────────────

    @Test
    void buscarPorEstado_normalizaAMinusculas() {
        when(asistenciaRepository.findAllByEstadoAsistencia("Ausente")).thenReturn(List.of());
        service.buscarAsistenciaPorEstado("AUSENTE");
        verify(asistenciaRepository).findAllByEstadoAsistencia("Ausente");
    }

    // ── reporteAsistenciaPorAlumno ────────────────────────────────────────────

    @Test
    void reporteAlumno_calculaPorcentajeCorrectamente() {
        MatriculaDTOInternal alumno = alumno(1L, 10L, "Juan Pérez", "11.111.111-1");
        when(academicoClient.listarMatriculasPorCurso(1L)).thenReturn(List.of(alumno));

        LocalDate desde = LocalDate.of(2026, 6, 1);
        LocalDate hasta = LocalDate.of(2026, 6, 30);

        List<Asistencia> registros = List.of(
                asistencia(1L, "Presente"),
                asistencia(1L, "Presente"),
                asistencia(1L, "Ausente"),
                asistencia(1L, "Justificado")
        );
        when(asistenciaRepository.findAllByIdMatriculaInAndFechaAsistenciaBetween(
                anyCollection(), eq(desde), eq(hasta))).thenReturn(registros);

        List<ReporteAlumnoDTO> resultado = service.reporteAsistenciaPorAlumno(1L, desde, hasta);

        assertThat(resultado).hasSize(1);
        ReporteAlumnoDTO dto = resultado.get(0);
        assertThat(dto.getTotalPresentes()).isEqualTo(2);
        assertThat(dto.getTotalAusentes()).isEqualTo(1);
        assertThat(dto.getTotalJustificados()).isEqualTo(1);
        // 2 presentes de 4 total = 50%
        assertThat(dto.getPorcentajeAsistencia()).isEqualTo(50.0);
    }

    @Test
    void reporteAlumno_sinRegistros_porcentajeCero() {
        MatriculaDTOInternal alumno = alumno(2L, 20L, "María González", "22.222.222-2");
        when(academicoClient.listarMatriculasPorCurso(1L)).thenReturn(List.of(alumno));
        when(asistenciaRepository.findAllByIdMatriculaInAndFechaAsistenciaBetween(
                anyCollection(), any(), any())).thenReturn(List.of());

        List<ReporteAlumnoDTO> resultado = service.reporteAsistenciaPorAlumno(
                1L, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPorcentajeAsistencia()).isEqualTo(0.0);
        assertThat(resultado.get(0).getTotalPresentes()).isZero();
    }

    @Test
    void reporteAlumno_todosPresentes_porcentajeCien() {
        MatriculaDTOInternal alumno = alumno(3L, 30L, "Carlos López", "33.333.333-3");
        when(academicoClient.listarMatriculasPorCurso(1L)).thenReturn(List.of(alumno));

        List<Asistencia> registros = List.of(
                asistencia(3L, "Presente"),
                asistencia(3L, "Presente"),
                asistencia(3L, "Presente")
        );
        when(asistenciaRepository.findAllByIdMatriculaInAndFechaAsistenciaBetween(
                anyCollection(), any(), any())).thenReturn(registros);

        List<ReporteAlumnoDTO> resultado = service.reporteAsistenciaPorAlumno(
                1L, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));

        assertThat(resultado.get(0).getPorcentajeAsistencia()).isEqualTo(100.0);
    }

    // ── reporteResumenPorCurso ────────────────────────────────────────────────

    @Test
    void reporteResumen_calculaTotalesYPorcentaje() {
        MatriculaDTOInternal alumno = alumno(1L, 10L, "Ana Torres", "44.444.444-4");
        when(academicoClient.listarMatriculasPorCurso(2L)).thenReturn(List.of(alumno));

        LocalDate desde = LocalDate.of(2026, 5, 1);
        LocalDate hasta = LocalDate.of(2026, 5, 31);

        List<Asistencia> registros = List.of(
                asistencia(1L, "Presente"),
                asistencia(1L, "Presente"),
                asistencia(1L, "Ausente")
        );
        when(asistenciaRepository.findAllByIdMatriculaInAndFechaAsistenciaBetween(
                anyCollection(), eq(desde), eq(hasta))).thenReturn(registros);

        ReporteAsistenciaResumenDTO dto = service.reporteResumenPorCurso(2L, desde, hasta);

        assertThat(dto.getTotalRegistros()).isEqualTo(3);
        assertThat(dto.getTotalPresentes()).isEqualTo(2);
        assertThat(dto.getTotalAusentes()).isEqualTo(1);
        assertThat(dto.getTotalJustificados()).isZero();
        // 2/3 ≈ 66.67%
        assertThat(dto.getPorcentajeAsistencia()).isCloseTo(66.67, org.assertj.core.data.Offset.offset(0.1));
    }

    @Test
    void reporteResumen_sinRegistros_porcentajeCero() {
        when(academicoClient.listarMatriculasPorCurso(1L)).thenReturn(List.of());
        when(asistenciaRepository.findAllByIdMatriculaInAndFechaAsistenciaBetween(
                anyCollection(), any(), any())).thenReturn(List.of());

        ReporteAsistenciaResumenDTO dto = service.reporteResumenPorCurso(
                1L, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));

        assertThat(dto.getTotalRegistros()).isZero();
        assertThat(dto.getPorcentajeAsistencia()).isEqualTo(0.0);
    }

    // ── reporteAsistenciaPorCursoYFecha ──────────────────────────────────────

    @Test
    void reporteDia_alumnoSinRegistro_retornaSinRegistro() {
        MatriculaDTOInternal alumno = alumno(5L, 50L, "Pedro Soto", "55.555.555-5");
        when(academicoClient.listarMatriculasPorCurso(1L)).thenReturn(List.of(alumno));
        when(asistenciaRepository.findAllByIdMatriculaInAndFechaAsistencia(
                anyCollection(), any())).thenReturn(List.of());

        List<ReporteAsistenciaDiaDTO> resultado = service.reporteAsistenciaPorCursoYFecha(
                1L, LocalDate.of(2026, 7, 7));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstadoAsistencia()).isEqualTo("sin registro");
    }

    @Test
    void reporteDia_alumnoConRegistro_retornaEstadoCorrecto() {
        MatriculaDTOInternal alumno = alumno(5L, 50L, "Pedro Soto", "55.555.555-5");
        when(academicoClient.listarMatriculasPorCurso(1L)).thenReturn(List.of(alumno));

        Asistencia registro = new Asistencia(10L, LocalDate.of(2026, 7, 7), null, "Presente", 5L);
        when(asistenciaRepository.findAllByIdMatriculaInAndFechaAsistencia(
                anyCollection(), any())).thenReturn(List.of(registro));

        List<ReporteAsistenciaDiaDTO> resultado = service.reporteAsistenciaPorCursoYFecha(
                1L, LocalDate.of(2026, 7, 7));

        assertThat(resultado.get(0).getEstadoAsistencia()).isEqualTo("Presente");
    }

    // ── registrarAsistenciaLote ───────────────────────────────────────────────

    @Test
    void registrarLote_matriculaNoPerteneceCurso_lanzaExcepcion() {
        LocalDate lunes = LocalDate.of(2026, 7, 6);
        when(fechaExcluidaRepository.findByFecha(lunes)).thenReturn(Optional.empty());
        when(periodoEscolarRepository.count()).thenReturn(0L);

        MatriculaDTOInternal alumnoValido = alumno(1L, 10L, "Ana Torres", "11.111.111-1");
        when(academicoClient.listarMatriculasPorCurso(1L)).thenReturn(List.of(alumnoValido));

        DetalleAsistenciaDTO detalleInvalido = new DetalleAsistenciaDTO();
        detalleInvalido.setIdMatricula(99L); // no existe en el roster
        detalleInvalido.setEstadoAsistencia("Presente");

        AsistenciaLoteRequestDTO request = new AsistenciaLoteRequestDTO();
        request.setIdCurso(1L);
        request.setFechaAsistencia(lunes);
        request.setDetalles(List.of(detalleInvalido));

        assertThatThrownBy(() -> service.registrarAsistenciaLote(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void registrarLote_fechaInvalida_lanzaExcepcion() {
        LocalDate sabado = LocalDate.of(2026, 7, 11);
        AsistenciaLoteRequestDTO request = new AsistenciaLoteRequestDTO();
        request.setFechaAsistencia(sabado);
        request.setIdCurso(1L);
        request.setDetalles(List.of());

        assertThatThrownBy(() -> service.registrarAsistenciaLote(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("fin de semana");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private MatriculaDTOInternal alumno(Long idMatricula, Long estudianteId, String nombre, String rut) {
        MatriculaDTOInternal m = new MatriculaDTOInternal();
        m.setIdMatricula(idMatricula);
        m.setEstudianteIdUsuario(estudianteId);
        m.setNombreEstudiante(nombre);
        m.setRutEstudiante(rut);
        return m;
    }

    private Asistencia asistencia(Long idMatricula, String estado) {
        Asistencia a = new Asistencia();
        a.setIdMatricula(idMatricula);
        a.setEstadoAsistencia(estado);
        a.setFechaAsistencia(LocalDate.now());
        return a;
    }
}
