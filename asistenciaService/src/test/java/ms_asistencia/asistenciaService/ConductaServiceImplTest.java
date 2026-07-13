package ms_asistencia.asistenciaService;

import ms_asistencia.asistenciaService.client.AcademicoClient;
import ms_asistencia.asistenciaService.client.MatriculaDTOInternal;
import ms_asistencia.asistenciaService.client.NotificacionClient;
import ms_asistencia.asistenciaService.client.UsuarioClient;
import ms_asistencia.asistenciaService.dto.ConductaResponseDTO;
import ms_asistencia.asistenciaService.dto.ReporteConductaAlumnoDTO;
import ms_asistencia.asistenciaService.model.Conducta;
import ms_asistencia.asistenciaService.repository.ConductaRepository;
import ms_asistencia.asistenciaService.services.impl.ConductaServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConductaServiceImplTest {

    @Mock private ConductaRepository conductaRepository;
    @Mock private UsuarioClient usuarioClient;
    @Mock private AcademicoClient academicoClient;
    @Mock private NotificacionClient notificacionClient;

    private ConductaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ConductaServiceImpl(conductaRepository, usuarioClient, academicoClient, notificacionClient);
    }

    // ── listarConductas ───────────────────────────────────────────────────────

    @Test
    void listar_retornaListaDelRepositorio() {
        List<Conducta> lista = List.of(
                conducta(1L, "positiva", "Excelente participación", 2L, 10L),
                conducta(2L, "negativa", "No entregó tarea", 2L, 10L)
        );
        when(conductaRepository.findAll()).thenReturn(lista);

        List<Conducta> resultado = service.listarConductas();
        assertThat(resultado).hasSize(2);
    }

    // ── buscarConductaPorId ───────────────────────────────────────────────────

    @Test
    void buscarPorId_existente_retornaConducta() {
        Conducta c = conducta(1L, "positiva", "Buena participación", 2L, 10L);
        when(conductaRepository.findById(1L)).thenReturn(Optional.of(c));

        Conducta resultado = service.buscarConductaPorId(1L);
        assertThat(resultado.getId_conducta()).isEqualTo(1L);
        assertThat(resultado.getTipoConducta()).isEqualTo("positiva");
    }

    @Test
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(conductaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarConductaPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ── eliminarConducta ──────────────────────────────────────────────────────

    @Test
    void eliminar_existente_invocaDelete() {
        Conducta c = conducta(1L, "negativa", "Interrumpió la clase", 2L, 10L);
        when(conductaRepository.findById(1L)).thenReturn(Optional.of(c));

        service.eliminarConducta(1L);
        verify(conductaRepository).delete(c);
    }

    @Test
    void eliminar_noExiste_lanzaExcepcion() {
        when(conductaRepository.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.eliminarConducta(5L))
                .isInstanceOf(RuntimeException.class);
    }

    // ── reporteResumenPorCurso ────────────────────────────────────────────────

    @Test
    void reporteResumen_contaCorrectamentePositivasYNegativas() {
        MatriculaDTOInternal alumno1 = alumno(1L, 10L, "Ana Torres", "11.111.111-1");
        MatriculaDTOInternal alumno2 = alumno(2L, 20L, "Luis Pino", "22.222.222-2");
        when(academicoClient.listarMatriculasPorCurso(1L)).thenReturn(List.of(alumno1, alumno2));

        List<Conducta> todas = List.of(
                conducta(1L, "positiva", "Excelente", 2L, 10L),
                conducta(2L, "positiva", "Participó", 2L, 10L),
                conducta(3L, "negativa", "Sin tarea", 2L, 10L),
                conducta(4L, "negativa", "Mala conducta", 2L, 20L)
        );
        when(conductaRepository.findAllByEstudianteIdUsuarioIn(anyCollection())).thenReturn(todas);

        List<ReporteConductaAlumnoDTO> resultado = service.reporteResumenPorCurso(1L);

        assertThat(resultado).hasSize(2);
        ReporteConductaAlumnoDTO ana = resultado.stream()
                .filter(r -> r.getEstudianteIdUsuario().equals(10L)).findFirst().orElseThrow();
        assertThat(ana.getTotalPositivas()).isEqualTo(2);
        assertThat(ana.getTotalNegativas()).isEqualTo(1);

        ReporteConductaAlumnoDTO luis = resultado.stream()
                .filter(r -> r.getEstudianteIdUsuario().equals(20L)).findFirst().orElseThrow();
        assertThat(luis.getTotalPositivas()).isZero();
        assertThat(luis.getTotalNegativas()).isEqualTo(1);
    }

    @Test
    void reporteResumen_cursoSinConductas_retornaCeros() {
        MatriculaDTOInternal alumno = alumno(1L, 10L, "Carlos Díaz", "33.333.333-3");
        when(academicoClient.listarMatriculasPorCurso(1L)).thenReturn(List.of(alumno));
        when(conductaRepository.findAllByEstudianteIdUsuarioIn(anyCollection())).thenReturn(List.of());

        List<ReporteConductaAlumnoDTO> resultado = service.reporteResumenPorCurso(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTotalPositivas()).isZero();
        assertThat(resultado.get(0).getTotalNegativas()).isZero();
    }

    // ── actualizarConducta ────────────────────────────────────────────────────

    @Test
    void actualizar_noExiste_lanzaExcepcion() {
        when(conductaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizarConducta(99L, new Conducta()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Conducta conducta(Long id, String tipo, String desc, Long docenteId, Long estudianteId) {
        return new Conducta(id, tipo, desc, LocalDate.now(), docenteId, estudianteId);
    }

    private MatriculaDTOInternal alumno(Long idMatricula, Long estudianteId, String nombre, String rut) {
        MatriculaDTOInternal m = new MatriculaDTOInternal();
        m.setIdMatricula(idMatricula);
        m.setEstudianteIdUsuario(estudianteId);
        m.setNombreEstudiante(nombre);
        m.setRutEstudiante(rut);
        return m;
    }
}
