# MS-Historial

Microservicio Spring Boot para la gestión de historiales médicos.

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.5**
- **Maven**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **OpenFeign**
- **Spring Validation**

## Arquitectura

El proyecto sigue una estructura en capas:

```
src/main/java/com/example/mshistorial/
```

- **controller/**: Controladores REST
- **service/**: Lógica de negocio
- **repository/**: Acceso a datos
- **model/**: Entidades JPA
- **dto/**: Objetos de transferencia de datos
- **client/**: Clientes Feign para comunicación entre microservicios
- **exception/**: Manejo de excepciones

## Configuración

### Base de Datos PostgreSQL

Crear la base de datos:

```sql
CREATE DATABASE historial_db;
```

### Configuración de Conexión

En `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/historial_db
spring.datasource.username=postgres
spring.datasource.password=password
```

## Endpoints API

### Historiales

- `POST /api/historiales` - Crear nuevo historial
- `GET /api/historiales/{id}` - Obtener historial por ID
- `GET /api/historiales/paciente/{pacienteId}` - Obtener historiales por paciente
- `GET /api/historiales/medico/{medicoId}` - Obtener historiales por médico
- `GET /api/historiales/tipo/{tipoHistorial}` - Obtener historiales por tipo
- `GET /api/historiales` - Obtener todos los historiales
- `PUT /api/historiales/{id}` - Actualizar historial
- `DELETE /api/historiales/{id}` - Eliminar historial

## Ejecución

```bash
mvn spring-boot:run
```

El servicio se ejecutará en el puerto 8083.

## Dependencias Principales

- Spring Web (REST APIs)
- Spring Data JPA (ORM)
- PostgreSQL Driver (Base de datos)
- Lombok (Reducción de código boilerplate)
- OpenFeign (Clientes HTTP)
- Spring Validation (Validación de datos)

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
|   |   |   |   |   |-- HistorialService.java
|   |   |   |   |   |-- HistorialServiceImpl.java
|   |   |   |   |-- repository/
|   |   |   |   |   |-- HistorialRepository.java
|   |   |   |   |-- model/
|   |   |   |   |   |-- Historial.java
|   |   |   |   |-- dto/
|   |   |   |   |   |-- HistorialDto.java
|   |   |   |   |-- client/
|   |   |   |   |   |-- PacienteClient.java
|   |   |   |   |   |-- MedicoClient.java
|   |   |   |   |-- exception/
|   |   |   |   |   |-- ResourceNotFoundException.java
|   |   |   |   |   |-- GlobalExceptionHandler.java
|   |   |   |   |-- MsHistorialApplication.java
|   |   |-- resources/
|   |   |   |-- application.properties
|   |-- test/
|-- pom.xml
|-- README.md
```

## Cliente Feign

El proyecto incluye clientes Feign para comunicarse con otros microservicios:

- **PacienteClient**: Para validar pacientes
- **MedicoClient**: Para validar médicos

## Validación

Se utiliza Spring Validation para validar los DTOs:

- `@NotNull`: Campos obligatorios
- `@NotBlank`: Cadenas no vacías
- Manejo global de excepciones de validación
