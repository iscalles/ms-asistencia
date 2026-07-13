# ms-asistencia — Colegio Bernardo O'Higgins

Microservicio de asistencia y conducta del sistema **Libro de Clases Digital** (DSY1106 Fullstack III).  
Permite registrar y consultar la asistencia diaria y las anotaciones de conducta de los alumnos, con validación de fechas hábiles y generación de reportes por curso.

---

## Responsabilidades

- Registrar asistencia individual o en lote (curso completo en una fecha)
- Validar que la fecha sea hábil (no finde, no fecha excluida, dentro del periodo escolar)
- Generar reportes de asistencia por día, por alumno y resumen de curso
- Registrar, consultar y reportar anotaciones de conducta (positivas/negativas)
- Gestionar el calendario escolar: fechas excluidas y periodos escolares
- Disparar notificaciones a través de ms-notificacion al registrar asistencia en lote

---

## Dependencias con otros microservicios

```
ms-asistencia → OpenFeign → ms-academico    (8083) → valida matrículas, obtiene roster del curso
ms-asistencia → OpenFeign → ms-usuario      (8081) → valida docentes y estudiantes
ms-asistencia → OpenFeign → ms-notificacion (8085) → notifica inasistencias a estudiante/apoderados
```

---

## Endpoints REST

### Asistencia — `/asistencia` (Puerto 8084)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/asistencia` | Lista todos los registros de asistencia |
| `GET` | `/asistencia/{id}` | Busca un registro por ID |
| `GET` | `/asistencia/estado/{estado}` | Filtra por estado (`presente`, `ausente`, `justificado`) |
| `GET` | `/asistencia/matricula/{idMatricula}` | Historial de asistencia de un alumno |
| `GET` | `/asistencia/curso/{idCurso}/roster` | Lista de alumnos matriculados en el curso (desde ms-academico) |
| `GET` | `/asistencia/curso/{idCurso}/reporte-dia?fecha=dd-MM-yyyy` | Reporte de asistencia del curso para una fecha |
| `GET` | `/asistencia/curso/{idCurso}/reporte-por-alumno?desde=dd-MM-yyyy&hasta=dd-MM-yyyy` | Reporte de asistencia por alumno en un rango de fechas |
| `GET` | `/asistencia/curso/{idCurso}/reporte-resumen?desde=dd-MM-yyyy&hasta=dd-MM-yyyy` | Totales y porcentaje de asistencia del curso |
| `GET` | `/asistencia/validar-fecha?fecha=dd-MM-yyyy` | Valida si una fecha es hábil para tomar asistencia |
| `POST` | `/asistencia` | Crea un registro de asistencia individual |
| `POST` | `/asistencia/lote` | Registra la asistencia de un curso completo en una fecha |
| `PUT` | `/asistencia/{id}` | Actualiza un registro de asistencia |
| `DELETE` | `/asistencia/{id}` | Elimina un registro de asistencia |

**Body de `POST /asistencia/lote`:**
```json
{
  "idCurso": 1,
  "fechaAsistencia": "18-06-2026",
  "detalles": [
    { "idMatricula": 10, "estadoAsistencia": "presente" },
    { "idMatricula": 11, "estadoAsistencia": "justificado", "justificacionAsistencia": "Certificado médico" }
  ]
}
```

### Conducta — `/conducta`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/conducta` | Lista todas las anotaciones |
| `GET` | `/conducta/{id}` | Busca una anotación por ID |
| `GET` | `/conducta/tipo/{tipo}` | Filtra por tipo de anotación |
| `GET` | `/conducta/estudiante/{estudianteIdUsuario}` | Historial de conducta de un alumno |
| `GET` | `/conducta/curso/{idCurso}/reporte-resumen` | Totales de anotaciones positivas/negativas por alumno del curso |
| `POST` | `/conducta` | Registra una anotación de conducta |
| `PUT` | `/conducta/{id}` | Actualiza una anotación |
| `DELETE` | `/conducta/{id}` | Elimina una anotación |

### Fechas Excluidas — `/fechas-excluidas`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/fechas-excluidas` | Lista todas las fechas excluidas (feriados, eventos) ordenadas por fecha |
| `POST` | `/fechas-excluidas` | Agrega una fecha excluida |
| `DELETE` | `/fechas-excluidas/{id}` | Elimina una fecha excluida |

### Periodos Escolares — `/periodos-escolares`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/periodos-escolares` | Lista todos los periodos escolares (orden desc por fecha inicio) |
| `POST` | `/periodos-escolares` | Agrega un periodo escolar |
| `DELETE` | `/periodos-escolares/{id}` | Elimina un periodo escolar |

---

## Validación de fecha hábil

Antes de registrar asistencia (individual o en lote), el servicio valida que la fecha:

1. No sea sábado ni domingo
2. No esté en la tabla `FECHA_EXCLUIDA` (feriados, suspensiones)
3. Esté dentro de un `PERIODO_ESCOLAR` activo

Si alguna condición falla, retorna `{ "esValida": false, "motivo": "..." }`.

---

## Configuración

```properties
# application.properties
spring.application.name=asistenciaService
server.port=8084

ms-academico.url=http://localhost:8083
ms-usuario.url=http://localhost:8081
ms-notificacion.url=http://localhost:8085

spring.datasource.url=jdbc:oracle:thin:@proyectolibroasistencia_high?TNS_ADMIN=<ruta_wallet>
spring.datasource.username=ms_asistencia
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver

# Pool reducido (tier gratuito compartido entre microservicios)
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=1

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
```

---

## Ejecución

```bash
# Desde la carpeta asistenciaService/
./mvnw spring-boot:run
```

> Requiere conectividad con Oracle Autonomous Database y el Wallet configurado.

---

## Tests unitarios

```bash
./mvnw test -Dtest="AsistenciaServiceImplTest,ConductaServiceImplTest" -Dsurefire.failIfNoSpecifiedTests=false
```

| Clase | Tests | Casos cubiertos |
|---|---|---|
| `AsistenciaServiceImplTest` | 17 | Validación de fechas (sábado, domingo, día hábil), buscar por ID, eliminar, filtrar por estado, reportes por alumno (0%, 50%, 100%), reporte resumen, reporte día, registro en lote con validaciones |
| `ConductaServiceImplTest` | 8 | Listar, buscar por ID, eliminar (existente y no existente), reporte resumen con positivas/negativas, actualizar no existente |

Los tests usan `@ExtendWith(MockitoExtension.class)` — no requieren base de datos ni Spring context.

---

## Patrones de diseño implementados

| Patrón | Implementación |
|---|---|
| **Repository** | Un repositorio JPA por entidad (`AsistenciaRepository`, `ConductaRepository`, `FechaExcluidaRepository`, `PeriodoEscolarRepository`) |
| **Service Layer** | Interfaces de servicio con implementaciones separadas (`ServiceImpl`) |
| **DTO** | `AsistenciaLoteRequestDTO` / `DetalleAsistenciaDTO` para registro en lote; DTOs de reporte (`ReporteAlumnoDTO`, `ReporteAsistenciaDiaDTO`, `ReporteAsistenciaResumenDTO`, `ReporteConductaAlumnoDTO`) |
| **Proxy (OpenFeign)** | `AcademicoClient`, `UsuarioClient`, `NotificacionClient` para comunicación con otros MS |
| **Exception Handler** | `@RestControllerAdvice` (`GlobalExceptionHandler`) — respuestas de error consistentes |

---

## Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje |
| Spring Boot | 3.2.12 | Framework base |
| Spring Data JPA | 3.x | Acceso a base de datos |
| Oracle Autonomous DB | — | Persistencia (esquema `ms_asistencia`) |
| OpenFeign | (via spring-cloud) | Comunicación con ms-academico, ms-usuario, ms-notificacion |
| JUnit 5 + Mockito | (via spring-boot-starter-test) | Tests unitarios |
