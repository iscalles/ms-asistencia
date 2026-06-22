# ms-asistencia — Microservicio de Asistencia y Conducta

Colegio Bernardo O'Higgins · Proyecto Libro de Clases Digital

Microservicio que permite a los docentes registrar y consultar la asistencia y las anotaciones de conducta de sus alumnos. Es consumido por el BFF y se comunica con ms-academico (matrículas/cursos) y ms-usuario (docentes/estudiantes) mediante OpenFeign.

---

## Responsabilidades

- Registrar la asistencia diaria de un alumno (presente / ausente / justificado)
- Tomar asistencia de un curso completo en una sola operación (registro en lote)
- Consultar el historial de asistencia de un alumno
- Registrar anotaciones de conducta (positivas/negativas) hechas por un docente a un alumno
- Consultar el historial de conducta de un alumno

---

## Requisitos previos

| Herramienta | Versión |
|---|---|
| Java JDK | 21 |
| Maven | 3.8 o superior |
| Oracle Autonomous Database | Wallet configurado |
| ms-academico | Corriendo en `http://localhost:8083` |
| ms-usuario | Corriendo en `http://localhost:8081` |

---

## Instalación y ejecución

```bash
# 1. Clonar el repositorio
git clone https://github.com/iscalles/ms-asistencia.git
cd ms-asistencia/asistenciaService

# 2. Copiar el wallet de Oracle a la ruta configurada
# El wallet debe estar en:
# src/main/resources/wallet/Wallet_proyectoLibroAsistencia/

# 3. Compilar
mvn clean package -DskipTests

# 4. Ejecutar
mvn spring-boot:run
```

---

## Configuración (`application.properties`)

```properties
spring.application.name=asistenciaService
server.port=8084

# Microservicios con los que se integra via OpenFeign
ms-academico.url=http://localhost:8083
ms-usuario.url=http://localhost:8081

# Base de datos Oracle (Autonomous Database)
spring.datasource.url=jdbc:oracle:thin:@proyectolibroasistencia_high?TNS_ADMIN=<ruta-wallet>
spring.datasource.username=ms_asistencia
spring.datasource.password=<contraseña>
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
spring.jpa.show-sql=true
```

> **Seguridad:** no subir credenciales reales al repositorio. `application.properties` no está versionado — cada integrante lo configura localmente.

---

## Endpoints REST

### Asistencia (`/asistencia`)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/asistencia` | Listar toda la asistencia |
| GET | `/asistencia/{id}` | Buscar registro de asistencia por ID |
| GET | `/asistencia/estado/{estado}` | Filtrar por estado (presente / ausente / justificado) |
| GET | `/asistencia/matricula/{idMatricula}` | Historial de asistencia de un alumno |
| GET | `/asistencia/curso/{idCurso}/roster` | Lista de alumnos matriculados en un curso (consulta a ms-academico), usada para armar la planilla de toma de asistencia |
| GET | `/asistencia/curso/{idCurso}/reporte-dia?fecha=dd-MM-yyyy` | Reporte de asistencia de un curso para una fecha puntual |
| GET | `/asistencia/curso/{idCurso}/reporte-resumen?desde=dd-MM-yyyy&hasta=dd-MM-yyyy` | Reporte resumido (totales y % de asistencia) de un curso en un rango de fechas |
| POST | `/asistencia` | Crear un registro de asistencia individual |
| POST | `/asistencia/lote` | Registrar la asistencia de un curso completo en una fecha (varios alumnos en una sola petición) |
| PUT | `/asistencia/{id}` | Actualizar un registro de asistencia |
| DELETE | `/asistencia/{id}` | Eliminar un registro de asistencia |

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

### Conducta (`/conducta`)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/conducta` | Listar todas las anotaciones de conducta |
| GET | `/conducta/{id}` | Buscar anotación por ID |
| GET | `/conducta/tipo/{tipo}` | Filtrar por tipo de anotación |
| GET | `/conducta/estudiante/{estudianteIdUsuario}` | Historial de conducta de un alumno |
| GET | `/conducta/curso/{idCurso}/reporte-resumen` | Reporte resumido de conducta (total anotaciones positivas/negativas) por alumno de un curso |
| POST | `/conducta` | Registrar una anotación de conducta |
| PUT | `/conducta/{id}` | Actualizar una anotación de conducta |
| DELETE | `/conducta/{id}` | Eliminar una anotación de conducta |

---

## Modelo de datos (tablas en `ms_asistencia`)

| Tabla | Descripción |
|---|---|
| `ASISTENCIA` | Estado de asistencia (presente/ausente/justificado) de un alumno en una fecha, referenciando su matrícula en ms-academico |
| `CONDUCTA` | Anotación de conducta hecha por un docente a un alumno, con tipo, descripción y fecha |

> `ASISTENCIA.MATRICULA_ID_MATRICULA` y `CONDUCTA.DOCENTE_ID_USUARIO` / `ESTUDIANTE_ID_USUARIO` son referencias externas (no FK reales), ya que apuntan a esquemas de otros microservicios. La integridad se valida en tiempo de ejecución vía OpenFeign.

---

## Comunicación con otros microservicios

```
ms-asistencia → OpenFeign → ms-academico (8083) → /matriculas/{id}, /matriculas/curso/{idCurso}
ms-asistencia → OpenFeign → ms-usuario (8081)   → /usuario/interno/{idUsuario}
```

- `AcademicoClient`: valida que una matrícula exista al registrar asistencia, y obtiene el roster de un curso para el registro en lote.
- `UsuarioClient`: valida que el docente y el estudiante existan al registrar una anotación de conducta.

---

## Patrones de diseño implementados

| Patrón | Implementación |
|---|---|
| **Repository** | Un repositorio JPA por entidad (`AsistenciaRepository`, `ConductaRepository`) |
| **Service Layer** | Interfaces de servicio con implementaciones separadas (`ServiceImpl`) |
| **DTO** | `AsistenciaLoteRequestDTO` / `DetalleAsistenciaDTO` para el registro en lote; `*DTOInternal` para las respuestas de otros microservicios |
| **Proxy (OpenFeign)** | `AcademicoClient` y `UsuarioClient` para validar entidades externas |
| **Exception Handler** | `@RestControllerAdvice` (`GlobalExceptionHandler`) — respuestas de error consistentes, sin HTTP 500 genéricos |

---

## Tecnologías

- Spring Boot 3.2.12
- Java 21
- Spring Data JPA + Hibernate
- Oracle Autonomous Database (esquema `ms_asistencia`)
- OpenFeign (comunicación con ms-academico y ms-usuario)
- Maven (arquetipo `spring-boot-starter-parent`)
