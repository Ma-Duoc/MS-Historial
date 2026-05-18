# MS-Historial

Microservicio Spring Boot para la gestión de historiales médicos en un sistema de salud distribuido.

## Descripción

El microservicio `ms-historial` es responsable de gestionar los registros médicos de pacientes, incluyendo diagnósticos, tratamientos y exámenes. Se integra con otros microservicios del sistema (ms-pacientes y ms-medicos) mediante OpenFeign y implementa patrones de resiliencia con Circuit Breaker.

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.5**
- **Maven**
- **Spring Data JPA**
- **H2 Database** (Base de datos en memoria)
- **Lombok**
- **OpenFeign** (Comunicación entre microservicios)
- **Spring Validation** (Validación de datos)
- **Resilience4j** (Circuit Breaker)
- **Spring Cloud 2023.0.1**

## Arquitectura

El proyecto sigue una arquitectura en capas con patrón de servicio único (sin interfaz + implementación):

```
src/main/java/com/example/mshistorial/
```

- **controller/**: Controladores REST API
- **service/**: Lógica de negocio (clases concretas con @Service)
- **repository/**: Acceso a datos con Spring Data JPA
- **model/**: Entidades JPA
- **dto/**: Objetos de transferencia de datos
- **client/**: Clientes Feign para comunicación entre microservicios
- **exception/**: Manejo centralizado de excepciones

## Configuración

### Base de Datos H2

El proyecto utiliza H2 en memoria para desarrollo y pruebas. La consola H2 está habilitada en `/h2-console`.

**Configuración en `application.properties`:**
```properties
spring.datasource.url=jdbc:h2:mem:historialdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Configuración del Servidor

- **Puerto**: 8085
- **URL base**: `http://localhost:8085`

### Configuración de Circuit Breaker

El proyecto implementa Circuit Breaker con Resilience4j para tres servicios:

**historialService** (para operaciones de creación/actualización):
- Ventana deslizante: 5 llamadas
- Umbral de fallos: 50%
- Tiempo en estado abierto: 10 segundos
- Mínimo de llamadas: 3
- Llamadas permitidas en estado semi-abierto: 2

**pacienteService** y **medicoService** (para validaciones externas):
- Ventana deslizante: 5 llamadas
- Umbral de fallos: 50%
- Tiempo en estado abierto: 10 segundos

### Configuración de Feign

```properties
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=5000
```

## Endpoints API

### Historiales Médicos

Base URL: `http://localhost:8085/historial`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/historial` | Crear nuevo historial médico |
| GET | `/historial/{id}` | Obtener historial por ID |
| GET | `/historial/paciente/{pacienteRut}` | Obtener historiales por RUT de paciente |
| GET | `/historial/medico/{medicoId}` | Obtener historiales por ID de médico |
| GET | `/historial` | Obtener todos los historiales |
| PUT | `/historial/{id}` | Actualizar historial existente |
| DELETE | `/historial/{id}` | Eliminar historial |

### Ejemplos de Uso

**Crear historial:**
```bash
curl -X POST http://localhost:8085/historial \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteRut": "12345678-9",
    "medicoId": 1,
    "diagnostico": "Hipertensión arterial",
    "tratamiento": "Amlodipina 5mg diarios",
    "examen": "Electrocardiograma normal"
  }'
```

**Obtener historiales por paciente:**
```bash
curl http://localhost:8085/historial/paciente/12345678-9
```

**Actualizar historial:**
```bash
curl -X PUT http://localhost:8085/historial/1 \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteRut": "12345678-9",
    "medicoId": 1,
    "diagnostico": "Hipertensión arterial controlada",
    "tratamiento": "Amlodipina 10mg diarios",
    "examen": "Electrocardiograma normal"
  }'
```

## Dependencias Principales

- **Spring Boot Starter Web**: REST APIs
- **Spring Boot Starter Data JPA**: ORM y acceso a datos
- **H2 Database**: Base de datos en memoria
- **Lombok**: Reducción de código boilerplate
- **Spring Cloud OpenFeign**: Clientes HTTP declarativos
- **Spring Boot Starter Validation**: Validación de datos
- **Spring Cloud Circuit Breaker Resilience4j**: Patrones de resiliencia
- **Spring Boot Starter Test**: Testing

## Estructura del Proyecto

```
ms-historial/
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |   |-- com/example/mshistorial/
|   |   |   |   |-- controller/
|   |   |   |   |   |-- HistorialController.java
|   |   |   |   |-- service/
|   |   |   |   |   |-- HistorialMedicoService.java
|   |   |   |   |-- repository/
|   |   |   |   |   |-- HistorialMedicoRepository.java
|   |   |   |   |-- model/
|   |   |   |   |   |-- HistorialMedico.java
|   |   |   |   |-- dto/
|   |   |   |   |   |-- HistorialMedicoDto.java
|   |   |   |   |   |-- PacienteDTO.java
|   |   |   |   |   |-- MedicoDTO.java
|   |   |   |   |-- client/
|   |   |   |   |   |-- PacienteClient.java
|   |   |   |   |   |-- MedicoClient.java
|   |   |   |   |-- exception/
|   |   |   |   |   |-- ResourceNotFoundException.java
|   |   |   |   |   |-- ValidationException.java
|   |   |   |   |   |-- GlobalExceptionHandler.java
|   |   |   |   |-- MsHistorialApplication.java
|   |   |-- resources/
|   |   |   |-- application.properties
|   |-- test/
|-- pom.xml
|-- README.md
```

## Integración con otros Microservicios

El microservicio se integra con:

### PacienteClient
- **URL**: `http://localhost:8082`
- **Propósito**: Validar existencia de pacientes
- **Endpoint**: `GET /api/pacientes/{rut}`

### MedicoClient
- **URL**: `http://localhost:8083`
- **Propósito**: Validar existencia de médicos
- **Endpoint**: `GET /api/medicos/{id}`

## Validación

El proyecto implementa validación en múltiples niveles:

### Validación de DTOs
- `@NotNull`: Campos obligatorios (pacienteRut, medicoId)
- Validación de negocio: Al menos uno de diagnóstico, tratamiento o examen debe estar presente

### Validación de Negocio
- Verificación de existencia de paciente en ms-pacientes
- Verificación de existencia de médico en ms-medicos
- Validaciones antes de crear o actualizar historiales

## Manejo de Excepciones

El proyecto incluye un manejador global de excepciones (`GlobalExceptionHandler`) que maneja:

- **ResourceNotFoundException** (404): Cuando un recurso no existe
- **ValidationException** (400): Errores de validación de negocio
- **MethodArgumentNotValidException** (400): Errores de validación de DTOs
- **RuntimeException** (503): Cuando servicios externos no están disponibles
- **Exception** (500): Errores generales del servidor

Todas las respuestas de error incluyen:
- Timestamp
- Status code
- Tipo de error
- Mensaje descriptivo

## Modelo de Datos

### HistorialMedico (Entidad)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Identificador único (auto-generado) |
| pacienteRut | String | RUT del paciente (obligatorio) |
| medicoId | Long | ID del médico (obligatorio) |
| medicoRut | String | RUT del médico (opcional) |
| diagnostico | String | Diagnóstico médico (hasta 2000 caracteres) |
| tratamiento | String | Tratamiento prescrito (hasta 2000 caracteres) |
| examen | String | Resultados de exámenes (hasta 2000 caracteres) |
| fechaCreacion | LocalDateTime | Fecha de creación (auto-generada) |
| fechaActualizacion | LocalDateTime | Fecha de última actualización (auto-generada) |

## Consultas Personalizadas

El repositorio incluye consultas personalizadas:

- `findByPacienteRut(String pacienteRut)`: Historiales por RUT de paciente
- `findByMedicoId(Long medicoId)`: Historiales por ID de médico
- `findByDiagnosticoIsNotNull()`: Historiales con diagnóstico
- `findByTratamientoIsNotNull()`: Historiales con tratamiento
- `findByExamenIsNotNull()`: Historiales con examen
- `findByPacienteRutAndMedicoId()`: Historiales por paciente y médico
- `findHistorialesCompletosPorPaciente()`: Historiales completos (con al menos un campo clínico)

## Circuit Breaker

El microservicio implementa el patrón Circuit Breaker para manejar fallos en servicios externos:

### Fallback Method
Cuando los servicios externos no están disponibles, se ejecuta el método `fallbackGeneral()` que lanza una excepción con el mensaje "Servicios externos no disponibles".

### Estados del Circuit Breaker
- **Cerrado**: Operación normal
- **Abierto**: Rechaza llamadas después de alcanzar el umbral de fallos
- **Semi-abierto**: Permite un número limitado de llamadas para probar recuperación


## Notas Adicionales

- La base de datos H2 se reinicia al detener la aplicación (datos no persistentes)
- La consola H2 está disponible en `http://localhost:8085/h2-console`
- Los microservicios externos (ms-pacientes, ms-medicos) deben estar disponibles para la validación completa



