# LMS Backend

A full-featured Learning Management System REST API backend — courses, quizzes, live classes with WebRTC, AI-powered question generation, and real-time communication, all in a single Spring Boot application.

---

## Architecture Diagram

```
 ┌──────────────────────────────────────────────────────────────────┐
 │                     Client (React / Mobile)                       │
 │           HTTP/REST          WebSocket (STOMP)    Socket.IO       │
 └──────┬───────────────────────────┬────────────────────┬──────────┘
        │                           │                    │
        ▼                           ▼                    ▼
 ┌─────────────────────────────────────────────────────────────────┐
 │                    Spring Boot Application                       │
 │                                                                  │
 │  ┌─────────────┐  ┌──────────────┐  ┌────────────────────────┐  │
 │  │ REST API    │  │ STOMP/WS     │  │ Socket.IO Server       │  │
 │  │ Controllers │  │ Controllers  │  │ (netty-socketio)       │  │
 │  └──────┬──────┘  └──────┬───────┘  └──────────┬─────────────┘  │
 │         │                │                      │ WebRTC signals  │
 │  ┌──────▼──────────────────────────────────────▼──────────────┐  │
 │  │                      Service Layer                          │  │
 │  │  Auth · Course · Quiz · AiQuiz · LiveClass · Progress ...  │  │
 │  └──────────────────────────┬───────────────────────────────┬─┘  │
 │                             │                               │     │
 │  ┌──────────────────────────▼───────────┐   ┌─────────────▼─┐   │
 │  │  Spring Data JPA Repositories        │   │  AI Service   │   │
 │  └──────────────────────────┬───────────┘   │  (external)   │   │
 │                             │               └───────────────┘   │
 └─────────────────────────────┼───────────────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │   PostgreSQL DB      │
                    └─────────────────────┘
```

---

## Why I Built This

Modern e-learning platforms (Moodle, Canvas) are powerful but heavyweight — hard to customise and slow to iterate on. This project is a clean-room, API-first LMS backend built to:

- Support the full student lifecycle: enroll → learn → quiz → track progress
- Enable instructors to run **live video classes** without a third-party service (peer-to-peer WebRTC via Socket.IO)
- Integrate **AI question generation** so instructors can build quizzes directly from lesson content
- Serve as a solid portfolio piece demonstrating real-world Spring Boot architecture patterns

---

## Key Technical Highlights

- **JWT Stateless Auth** — Every request is authenticated via an `Authorization: Bearer <token>` header. Tokens are signed with HS512 and validated by a custom `JwtAuthenticationFilter` that sits in front of Spring Security's filter chain.
- **Dual Real-Time Layer** — STOMP over WebSocket handles live-class chat/events; a separate embedded **netty-socketio** server brokers WebRTC signalling (offer/answer/ICE) for peer-to-peer video between students.
- **AI-Powered Quiz Generation** — Instructors can call `POST /api/quizzes/lessons/{lessonId}/generate-questions` to hit a configurable AI microservice, which returns MCQ questions auto-generated from lesson text. Questions are immediately importable into any quiz.
- **Quiz Variants (Anti-Cheat)** — The quiz engine can generate N randomised variants of a quiz (shuffled questions & options), ensuring each student receives a unique ordering during exams.
- **Fine-Grained Course Security** — A dedicated `CourseSecurityService` is injected into Spring Security's `@PreAuthorize` expressions, letting individual endpoints check whether the calling user is enrolled, an instructor of that course, or a system admin — without polluting business logic.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.4 |
| Security | Spring Security + JWT (jjwt 0.11.5, HS512) |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Real-Time (events/chat) | Spring WebSocket (STOMP) |
| Real-Time (WebRTC signalling) | netty-socketio 1.7.19 |
| Validation | Spring Validation + javax.validation |
| Code Generation | Lombok |
| Build | Maven (wrapper included) |
| AI Integration | External HTTP microservice (configurable URL) |

---

## How to Run Locally

### Prerequisites

| Requirement | Version |
|---|---|
| JDK | 17+ |
| PostgreSQL | 14+ |
| (Optional) AI service | any HTTP server at configured URL |

### 1 — Clone & configure

```sh
git clone https://github.com/deepgodhani/lms-backend.git
cd lms-backend
```

Open `src/main/resources/application.properties` and set your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/lms_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD          # replace with your actual password
app.jwt.secret=YOUR_BASE64_SECRET                 # replace the placeholder
app.jwt.expiration-ms=2592000000                  # 30 days (adjust as needed)
ai.service.url=http://localhost:8000/generate-questions  # AI microservice URL
```

> **Note:** Hibernate is set to `ddl-auto=update`, so the schema is created/updated automatically on first startup. Create the `lms_db` database manually before running.

### 2 — Create the database

```sql
CREATE DATABASE lms_db;
```

### 3 — Run the application

**Linux / macOS:**
```sh
./mvnw spring-boot:run
```

**Windows:**
```bat
mvnw.cmd spring-boot:run
```

The REST API is available at `http://localhost:8080/api/`.  
The embedded Socket.IO server starts on its configured port automatically.

### 4 — Build a deployable JAR

```sh
./mvnw clean package
java -jar target/*.jar
```

### 5 — Run tests

```sh
./mvnw test
```

---

## API Overview

| Resource | Base Path | Notable Endpoints |
|---|---|---|
| Auth | `/api/auth` | `POST /register`, `POST /login` |
| Courses | `/api/courses` | CRUD, enroll, get role |
| Modules | `/api/modules` | CRUD, add lessons |
| Lessons | `/api/lessons` | CRUD, update content |
| Quizzes | `/api/quizzes` | CRUD, submit, results, generate variants |
| Questions | `/api/questions` | CRUD |
| Assignments | `/api/assignments` | CRUD, submit |
| Announcements | `/api/announcements` | CRUD |
| Discussions | `/api/discussions` | Threads + posts |
| Live Classes | `/api/live-classes` | Create, start, end |
| Progress | `/api/lessons/{id}/complete` | Mark lesson complete |
| Users | `/api/users` | `/me`, `/my-courses` |

All endpoints except `/api/auth/**` and `GET /api/courses/all-courses` require a valid JWT.

---

## Architecture Overview

The application follows a classic **layered architecture**:

```
Controller → Service → Repository → Database
```

- **`config/`** — Spring Security filter chain, JWT filter, WebSocket/Socket.IO config, CORS, and a `DataInitializer` that seeds roles on startup.
- **`controller/`** — Thin REST controllers. Input validation via `@Valid`; business rules delegated to services.
- **`service/`** — All business logic lives here. `CourseSecurityService` is used inline in `@PreAuthorize` annotations for method-level security. `AiQuizService` is the HTTP client for the external AI microservice. `SocketIOService` owns the WebRTC signalling room map.
- **`repository/`** — Spring Data JPA interfaces; no custom SQL except where complex queries require it.
- **`model/`** — JPA entities. Key hierarchy: `Course → Module → Lesson`. `Quiz` belongs to a `Course`; `QuizVariant` holds a shuffled copy of questions. `LiveClass` has a `LiveClassStatus` enum (SCHEDULED / ACTIVE / ENDED).
- **`dto/`** — Separate request/response objects keep the API contract decoupled from entity changes.
- **`exception/`** — A `GlobalExceptionHandler` (`@RestControllerAdvice`) centralises error responses.

---

## Known Limitations & What I'd Improve

| # | Limitation | Improvement |
|---|---|---|
| 1 | **Socket.IO room state is in-memory** — restarting the server drops all active live-class participants. | Move room/session state to Redis so the service can scale horizontally. |
| 2 | **AI question generation uses naive string interpolation** for the JSON request body. | Use a proper `ObjectMapper` to serialise the request to avoid JSON injection from lesson content. |
| 3 | **No file upload support** — assignment submissions are text-only. | Add S3/MinIO-backed file storage for PDF and video submissions. |
| 4 | **No refresh token mechanism** — JWT expiry (30 days) means users stay logged in very long or get abruptly logged out. | Implement short-lived access tokens + long-lived refresh tokens with rotation. |
| 5 | **CORS locked to `localhost:3000`** — not configurable without a code change. | Externalise allowed origins to a property so different environments (staging, production) can be configured without rebuilding. |
