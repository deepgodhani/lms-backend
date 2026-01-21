# LMS Backend

Spring Boot backend for the LMS. Provides REST APIs and starts a Socket.IO server on application startup.

## Tech Stack
- Java + Spring Boot
- Maven
- Socket.IO (server started via `SocketIOService`)

## Project Structure
- `src/main/java/com/versionxd/lms/backend`
  - `BackendApplication.java` (entry point)
  - `config/` (app/security/config classes)
  - `controller/` (REST controllers)
  - `service/` (business logic + Socket.IO service)
  - `repository/` (data access)
  - `model/` (entities / domain objects)
  - `dto/` (request/response DTOs)
  - `mapper/` (mapping layer)
  - `exception/` (error handling)
- `src/main/resources`
  - `application.properties`
  - `application-local.properties`
- `src/test/java/...`
  - tests

## Requirements
- Java (JDK 17 recommended)
- Maven (or use the included Maven Wrapper: `mvnw`, `mvnw.cmd`)

## Configuration
The project uses Spring Boot property files:
- `src/main/resources/application.properties`
- `src/main/resources/application-local.properties`

Common values you may need to set (depending on your environment):
- server port
- database connection
- JWT settings
- Socket.IO host/port settings (if exposed via properties)

## Run (Local)
Using Maven Wrapper:

```sh
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

## Build
```sh
./mvnw clean package
```

The jar will be produced under `target/`.

## Tests
```sh
./mvnw test
```

## Socket.IO Server
On startup the application runs a `CommandLineRunner` that starts the Socket.IO server via:
- [`com.versionxd.lms.backend.service.SocketIOService`](src/main/java/com/versionxd/lms/backend/service/SocketIOService.java)
- Initialized in [`com.versionxd.lms.backend.BackendApplication#runner`](src/main/java/com/versionxd/lms/backend/BackendApplication.java)

If the app starts but “hangs”, verify the Socket.IO server port is available and configured correctly.

## Notes
- Configuration, JWT filter, and initialization logic live under `src/main/java/.../config`.
- REST endpoints are implemented under `controller/`.

