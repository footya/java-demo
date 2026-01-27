<!-- OPENSPEC:START -->
# OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->

## Build & Test Commands

### Core Commands
- **Build package**: `mvn -q -DskipTests package`
- **Run dev server**: `mvn -q spring-boot:run` or `./dev.sh <AMAP_KEY> [port]`
- **Run JAR**: `java -jar target/java-demo-0.0.1-SNAPSHOT.jar`
- **Quick dev**: `./start.sh <AMAP_KEY> [port]`

### Testing
- This project currently has no test directory (`src/test/` does not exist)
- When adding tests, use JUnit 5 (standard with Spring Boot 3.2.1)
- Single test example (future): `mvn test -Dtest=ClassName#methodName`

## Code Style Guidelines

### Layered Architecture
- **Controller**: Protocol layer only - routing, param parsing/validation, DTO conversion, response wrapping. No business logic.
- **Service**: Business logic and orchestration. Keep methods short and intent-clear. Use domain objects, not DTOs.
- **Client classes**: External HTTP/API calls wrapped in dedicated client classes. Handle timeout, failure, degradation, retry explicitly.
- **DTO**: For interface input/output. Field naming matches JSON mapping. Domain objects not exposed as API returns.

### Naming Conventions
- Packages and classes: Semantic meaning, avoid abbreviation stacking
- DTOs: Clear purpose, consistent with JSON field names
- Domain objects: Internal business models, separate from protocol DTOs

### Import Organization
- Group imports: standard library, third-party, then internal project
- Use `import com.luckin.javademo.*;` sparingly - prefer explicit imports
- Avoid wildcard imports for third-party packages

### Type & Language Features
- Java 17 required
- Use `java.time` types (Instant, OffsetDateTime, LocalDateTime) - avoid Date/Calendar
- Use `record` for immutable data carriers (DTOs, responses, internal results)
- Avoid mutable static/global state in controllers/services
- Use `@Value` for configuration injection, not `@Autowired` on fields

### Parameter Validation
- Use Jakarta Bean Validation (`@Valid` + constraints like `@NotBlank`, `@NotNull`) at Controller layer
- Validate request parameters in Controller, return 400 for invalid input
- Service methods assume valid input - focus on business logic only

### Error Handling
- Centralized via `@RestControllerAdvice` (see `GlobalExceptionHandler`)
- Return structured error responses with `code/message/traceId/path/timestamp`
- Never expose internal details: no stack traces, class names, SQL, URLs, or tokens in client responses
- Use standard Spring exceptions: `ResponseStatusException`, `MethodArgumentNotValidException`
- For 4xx: return generic client-friendly messages
- For 5xx: return "系统繁忙，请稍后再试", log full stack trace with traceId

### Logging (SLF4J)
- Use parameterized logging: `log.info("city={}, temp={}", city, temp)` - not string concatenation
- Log key fields for searchability, not long sentences
- Include context in error logs: traceId, method, path
- **No System.out.println** - use logger
- **Sensitive data**: Never log tokens, keys, phone numbers, IDs - mask or omit

### Security
- No hardcoded secrets: use `application.properties`/`application.yml` or environment variables
- External API keys injected via `@Value("${amap.key:}")` pattern
- Log desensitization: replace sensitive parts with asterisks

### Comment Style (Chinese)
- Add generous Chinese comments for: class/method responsibilities, key business branches, protocol field meanings, exception causes and handling strategies
- Avoid low-value comments that just restate what code does
- Comment boundary conditions and edge cases
- Explain why, not just what

### Verification
- Every change must pass: `mvn -q -DskipTests package`
- Make minimal changes to solve the requirement - don't refactor/format entire project unless requested
- Avoid unnecessary frameworks or patterns - prefer simple, readable, testable code

### References
- Google Java Style Guide: https://google.github.io/styleguide/javaguide.html
- Alibaba Java Manual: https://github.com/alibaba/p3c
- Spring Boot Reference: https://docs.spring.io/spring-boot/reference/
- Jakarta Bean Validation: https://jakarta.ee/specifications/bean-validation/
