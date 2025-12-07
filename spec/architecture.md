# Hexagonal Architecture Migration Plan

This document outlines the plan to migrate the Notes API from a layered architecture to hexagonal architecture (ports and adapters).

## What is Hexagonal Architecture?

Hexagonal Architecture (Ports & Adapters), created by Alistair Cockburn, isolates your **core business logic** from external concerns (HTTP, databases, messaging). The key principle: **dependencies point inward** - the domain knows nothing about infrastructure.

```
                    ┌─────────────────────────────────────────┐
                    │            INFRASTRUCTURE               │
                    │  ┌─────────────────────────────────────┐│
                    │  │          APPLICATION                ││
                    │  │  ┌─────────────────────────────────┐││
   REST ────────────┼──┼──┤    (Pure Business Logic)        │││
   CLI  ────────────┼──┼──┤    - Entities                   │├┼──────── PostgreSQL
   gRPC ────────────┼──┼──┤    - Value Objects              │││──────── Redis
                    │  │  │    - Domain Services            │││──────── S3
                    │  │  └─────────────────────────────────┘││
                    │  │     Ports (Interfaces)              ││
                    │  │     Use Cases                       ││
                    │  └─────────────────────────────────────┘│
                    │     Adapters (Implementations)          │
                    └─────────────────────────────────────────┘
```

## Current State Analysis

The codebase currently follows a layered architecture:

| Current Package | Current Role | Hexagonal Equivalent |
|-----------------|--------------|---------------------|
| `entity/Note.java` | JPA Entity with Panache | Domain Model (needs cleanup) |
| `service/NoteService.java` | Business logic | Application Service |
| `repository/NoteRepository.java` | Data access | Outbound Adapter |
| `resource/NoteResource.java` | REST handler | Inbound Adapter |
| `api/NoteApi.java` | REST contract | Inbound Port interface |
| `dto/*` | Request/Response DTOs | Adapter-specific DTOs |

### Key Issues to Address

1. Domain entity (`Note`) is polluted with JPA annotations - framework leakage
2. Repository is directly used by service - no abstraction
3. No clear boundary between domain and application layer
4. DTOs live outside the adapters that use them

## Target Package Structure

```
src/main/java/com/example/notes/
├── domain/                          # CORE - Zero dependencies on frameworks
│   ├── model/
│   │   ├── Note.java               # Pure domain entity (no JPA)
│   │   ├── NoteId.java             # Value object for ID
│   │   └── Tag.java                # Value object for tags
│   ├── exception/
│   │   ├── NoteNotFoundException.java
│   │   └── NoteValidationException.java
│   └── service/                    # Domain services (optional for complex logic)
│       └── NoteValidator.java      # If validation rules become complex
│
├── application/                     # USE CASES - Orchestrates domain
│   ├── port/
│   │   ├── in/                     # Primary/Driving ports (what app offers)
│   │   │   ├── CreateNoteUseCase.java
│   │   │   ├── GetNoteUseCase.java
│   │   │   ├── UpdateNoteUseCase.java
│   │   │   ├── DeleteNoteUseCase.java
│   │   │   └── ListNotesUseCase.java
│   │   └── out/                    # Secondary/Driven ports (what app needs)
│   │       └── NoteRepository.java  # Interface only, no Panache
│   └── service/
│       └── NoteApplicationService.java  # Implements all use cases
│
└── infrastructure/                  # ADAPTERS - Framework-specific code
    ├── adapter/
    │   ├── in/
    │   │   └── rest/               # REST adapter (primary/inbound)
    │   │       ├── NoteRestAdapter.java    # JAX-RS resource
    │   │       ├── NoteApiContract.java    # OpenAPI interface
    │   │       ├── dto/
    │   │       │   ├── CreateNoteRequest.java
    │   │       │   ├── UpdateNoteRequest.java
    │   │       │   └── NoteResponse.java
    │   │       ├── mapper/
    │   │       │   └── NoteRestMapper.java  # DTO ↔ Domain mapping
    │   │       └── exception/
    │   │           └── RestExceptionHandler.java
    │   └── out/
    │       └── persistence/        # Database adapter (secondary/outbound)
    │           ├── JpaNoteRepository.java  # Implements port, uses Panache
    │           ├── NoteJpaEntity.java      # JPA-annotated entity
    │           └── mapper/
    │               └── NotePersistenceMapper.java  # JPA ↔ Domain mapping
    └── config/
        └── BeanConfiguration.java  # CDI producer methods if needed
```

## Migration Phases

### Phase 1: Create Pure Domain Layer

**Goal:** Extract a clean domain model with zero framework dependencies.

#### Step 1.1: Create Domain Model

Create `domain/model/NoteId.java` (Value Object):

```java
public record NoteId(UUID value) {
    public NoteId {
        Objects.requireNonNull(value, "NoteId cannot be null");
    }

    public static NoteId generate() {
        return new NoteId(UUID.randomUUID());
    }

    public static NoteId from(String value) {
        return new NoteId(UUID.fromString(value));
    }
}
```

Create `domain/model/Note.java` (Pure Domain Entity):

```java
public class Note {
    private final NoteId id;
    private String title;
    private String content;
    private List<String> tags;
    private final Instant createdAt;
    private Instant updatedAt;

    // Private constructor - use factory methods
    private Note(NoteId id, String title, String content,
                 List<String> tags, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory for new notes
    public static Note create(String title, String content, List<String> tags) {
        validate(title, content);
        Instant now = Instant.now();
        return new Note(NoteId.generate(), title, content, tags, now, now);
    }

    // Factory for reconstitution from persistence
    public static Note reconstitute(NoteId id, String title, String content,
                                     List<String> tags, Instant createdAt,
                                     Instant updatedAt) {
        return new Note(id, title, content, tags, createdAt, updatedAt);
    }

    // Domain behavior
    public void updateContent(String title, String content, List<String> tags) {
        validate(title, content);
        this.title = title;
        this.content = content;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.updatedAt = Instant.now();
    }

    private static void validate(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new NoteValidationException("Title cannot be blank");
        }
        if (content == null || content.isBlank()) {
            throw new NoteValidationException("Content cannot be blank");
        }
    }

    // Getters (no setters - immutable where possible)
    public NoteId getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getTags() { return List.copyOf(tags); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
```

#### Step 1.2: Create Domain Exceptions

```java
// domain/exception/NoteNotFoundException.java
public class NoteNotFoundException extends RuntimeException {
    private final NoteId noteId;

    public NoteNotFoundException(NoteId noteId) {
        super("Note not found: " + noteId.value());
        this.noteId = noteId;
    }

    public NoteId getNoteId() { return noteId; }
}

// domain/exception/NoteValidationException.java
public class NoteValidationException extends RuntimeException {
    public NoteValidationException(String message) {
        super(message);
    }
}
```

### Phase 2: Define Application Ports

**Goal:** Create interfaces that define what the application offers (inbound) and needs (outbound).

#### Step 2.1: Inbound Ports (Use Cases)

```java
// application/port/in/CreateNoteUseCase.java
public interface CreateNoteUseCase {
    Note execute(CreateNoteCommand command);

    record CreateNoteCommand(String title, String content, List<String> tags) {}
}

// application/port/in/GetNoteUseCase.java
public interface GetNoteUseCase {
    Note execute(NoteId id);
}

// application/port/in/ListNotesUseCase.java
public interface ListNotesUseCase {
    List<Note> execute();
}

// application/port/in/UpdateNoteUseCase.java
public interface UpdateNoteUseCase {
    Note execute(UpdateNoteCommand command);

    record UpdateNoteCommand(NoteId id, String title,
                             String content, List<String> tags) {}
}

// application/port/in/DeleteNoteUseCase.java
public interface DeleteNoteUseCase {
    void execute(NoteId id);
}
```

#### Step 2.2: Outbound Port (Repository)

```java
// application/port/out/NoteRepository.java
public interface NoteRepository {
    Note save(Note note);
    Optional<Note> findById(NoteId id);
    List<Note> findAll();
    void deleteById(NoteId id);
    boolean existsById(NoteId id);
}
```

### Phase 3: Implement Application Services

**Goal:** Implement use cases that orchestrate domain logic.

```java
// application/service/NoteApplicationService.java
@ApplicationScoped
public class NoteApplicationService implements
        CreateNoteUseCase,
        GetNoteUseCase,
        ListNotesUseCase,
        UpdateNoteUseCase,
        DeleteNoteUseCase {

    private final NoteRepository noteRepository;

    public NoteApplicationService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    @Transactional
    public Note execute(CreateNoteCommand command) {
        Note note = Note.create(
            command.title(),
            command.content(),
            command.tags()
        );
        return noteRepository.save(note);
    }

    @Override
    public Note execute(NoteId id) {
        return noteRepository.findById(id)
            .orElseThrow(() -> new NoteNotFoundException(id));
    }

    @Override
    public List<Note> execute() {
        return noteRepository.findAll();
    }

    @Override
    @Transactional
    public Note execute(UpdateNoteCommand command) {
        Note note = noteRepository.findById(command.id())
            .orElseThrow(() -> new NoteNotFoundException(command.id()));

        note.updateContent(command.title(), command.content(), command.tags());
        return noteRepository.save(note);
    }

    @Override
    @Transactional
    public void execute(NoteId id) {
        if (!noteRepository.existsById(id)) {
            throw new NoteNotFoundException(id);
        }
        noteRepository.deleteById(id);
    }
}
```

### Phase 4: Create Infrastructure Adapters

#### Step 4.1: Persistence Adapter (Outbound)

```java
// infrastructure/adapter/out/persistence/NoteJpaEntity.java
@Entity
@Table(name = "notes")
public class NoteJpaEntity extends PanacheEntityBase {
    @Id
    @UuidGenerator
    public UUID id;

    @Column(nullable = false)
    public String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String content;

    @Column(name = "tags", columnDefinition = "TEXT[]")
    public List<String> tags;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;
}
```

```java
// infrastructure/adapter/out/persistence/mapper/NotePersistenceMapper.java
@ApplicationScoped
public class NotePersistenceMapper {

    public NoteJpaEntity toJpaEntity(Note note) {
        NoteJpaEntity entity = new NoteJpaEntity();
        entity.id = note.getId().value();
        entity.title = note.getTitle();
        entity.content = note.getContent();
        entity.tags = new ArrayList<>(note.getTags());
        entity.createdAt = note.getCreatedAt();
        entity.updatedAt = note.getUpdatedAt();
        return entity;
    }

    public Note toDomain(NoteJpaEntity entity) {
        return Note.reconstitute(
            new NoteId(entity.id),
            entity.title,
            entity.content,
            entity.tags,
            entity.createdAt,
            entity.updatedAt
        );
    }
}
```

```java
// infrastructure/adapter/out/persistence/JpaNoteRepository.java
@ApplicationScoped
public class JpaNoteRepository implements NoteRepository {

    private final NotePersistenceMapper mapper;

    public JpaNoteRepository(NotePersistenceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Note save(Note note) {
        NoteJpaEntity entity = mapper.toJpaEntity(note);
        entity.persist();
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Note> findById(NoteId id) {
        return NoteJpaEntity.<NoteJpaEntity>findByIdOptional(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public List<Note> findAll() {
        return NoteJpaEntity.<NoteJpaEntity>findAll()
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(NoteId id) {
        NoteJpaEntity.deleteById(id.value());
    }

    @Override
    public boolean existsById(NoteId id) {
        return NoteJpaEntity.findByIdOptional(id.value()).isPresent();
    }
}
```

#### Step 4.2: REST Adapter (Inbound)

```java
// infrastructure/adapter/in/rest/dto/CreateNoteRequest.java
public record CreateNoteRequest(
    @NotBlank String title,
    @NotBlank String content,
    List<String> tags
) {}

// infrastructure/adapter/in/rest/dto/NoteResponse.java
public record NoteResponse(
    UUID id,
    String title,
    String content,
    List<String> tags,
    Instant createdAt,
    Instant updatedAt
) {}
```

```java
// infrastructure/adapter/in/rest/mapper/NoteRestMapper.java
@ApplicationScoped
public class NoteRestMapper {

    public CreateNoteUseCase.CreateNoteCommand toCommand(CreateNoteRequest request) {
        return new CreateNoteUseCase.CreateNoteCommand(
            request.title(),
            request.content(),
            request.tags()
        );
    }

    public UpdateNoteUseCase.UpdateNoteCommand toCommand(UUID id, UpdateNoteRequest request) {
        return new UpdateNoteUseCase.UpdateNoteCommand(
            new NoteId(id),
            request.title(),
            request.content(),
            request.tags()
        );
    }

    public NoteResponse toResponse(Note note) {
        return new NoteResponse(
            note.getId().value(),
            note.getTitle(),
            note.getContent(),
            note.getTags(),
            note.getCreatedAt(),
            note.getUpdatedAt()
        );
    }
}
```

```java
// infrastructure/adapter/in/rest/NoteRestAdapter.java
@Path("/v1/notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NoteRestAdapter {

    private final CreateNoteUseCase createNoteUseCase;
    private final GetNoteUseCase getNoteUseCase;
    private final ListNotesUseCase listNotesUseCase;
    private final UpdateNoteUseCase updateNoteUseCase;
    private final DeleteNoteUseCase deleteNoteUseCase;
    private final NoteRestMapper mapper;

    public NoteRestAdapter(
            CreateNoteUseCase createNoteUseCase,
            GetNoteUseCase getNoteUseCase,
            ListNotesUseCase listNotesUseCase,
            UpdateNoteUseCase updateNoteUseCase,
            DeleteNoteUseCase deleteNoteUseCase,
            NoteRestMapper mapper) {
        this.createNoteUseCase = createNoteUseCase;
        this.getNoteUseCase = getNoteUseCase;
        this.listNotesUseCase = listNotesUseCase;
        this.updateNoteUseCase = updateNoteUseCase;
        this.deleteNoteUseCase = deleteNoteUseCase;
        this.mapper = mapper;
    }

    @POST
    public Response createNote(@Valid CreateNoteRequest request) {
        var command = mapper.toCommand(request);
        Note note = createNoteUseCase.execute(command);
        return Response.status(Response.Status.CREATED)
            .entity(mapper.toResponse(note))
            .build();
    }

    @GET
    public List<NoteResponse> listNotes() {
        return listNotesUseCase.execute().stream()
            .map(mapper::toResponse)
            .toList();
    }

    @GET
    @Path("/{id}")
    public NoteResponse getNote(@PathParam("id") UUID id) {
        Note note = getNoteUseCase.execute(new NoteId(id));
        return mapper.toResponse(note);
    }

    @PUT
    @Path("/{id}")
    public NoteResponse updateNote(@PathParam("id") UUID id,
                                   @Valid UpdateNoteRequest request) {
        var command = mapper.toCommand(id, request);
        Note note = updateNoteUseCase.execute(command);
        return mapper.toResponse(note);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteNote(@PathParam("id") UUID id) {
        deleteNoteUseCase.execute(new NoteId(id));
        return Response.noContent().build();
    }
}
```

#### Step 4.3: Exception Handler

```java
// infrastructure/adapter/in/rest/exception/RestExceptionHandler.java
@Provider
public class RestExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof NoteNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
        if (exception instanceof NoteValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(Map.of("error", "Internal server error"))
            .build();
    }
}
```

### Phase 5: Update Tests

#### Step 5.1: Domain Unit Tests (Pure, No Framework)

```java
// test/java/com/example/notes/domain/model/NoteTest.java
class NoteTest {

    @Test
    void shouldCreateNoteWithGeneratedId() {
        Note note = Note.create("Title", "Content", List.of("tag1"));

        assertNotNull(note.getId());
        assertEquals("Title", note.getTitle());
        assertEquals("Content", note.getContent());
        assertEquals(List.of("tag1"), note.getTags());
    }

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(NoteValidationException.class,
            () -> Note.create("", "Content", null));
    }

    @Test
    void shouldUpdateContent() {
        Note note = Note.create("Title", "Content", null);
        Instant originalUpdatedAt = note.getUpdatedAt();

        note.updateContent("New Title", "New Content", List.of("new-tag"));

        assertEquals("New Title", note.getTitle());
        assertTrue(note.getUpdatedAt().isAfter(originalUpdatedAt) ||
                   note.getUpdatedAt().equals(originalUpdatedAt));
    }
}
```

#### Step 5.2: Application Service Tests (With Mocked Port)

```java
// test/java/com/example/notes/application/service/NoteApplicationServiceTest.java
@QuarkusTest
class NoteApplicationServiceTest {

    @InjectMock
    NoteRepository noteRepository;

    @Inject
    NoteApplicationService service;

    @Test
    void shouldCreateNote() {
        var command = new CreateNoteUseCase.CreateNoteCommand(
            "Title", "Content", List.of("tag1"));

        when(noteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Note result = service.execute(command);

        assertEquals("Title", result.getTitle());
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void shouldThrowWhenNoteNotFound() {
        NoteId id = NoteId.generate();
        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class,
            () -> service.execute(id));
    }
}
```

#### Step 5.3: Integration Tests

Keep existing REST Assured tests but update paths and adapt to new structure.

### Phase 6: File Changes Summary

#### Files to Create

```
domain/model/Note.java
domain/model/NoteId.java
domain/exception/NoteNotFoundException.java
domain/exception/NoteValidationException.java
application/port/in/CreateNoteUseCase.java
application/port/in/GetNoteUseCase.java
application/port/in/ListNotesUseCase.java
application/port/in/UpdateNoteUseCase.java
application/port/in/DeleteNoteUseCase.java
application/port/out/NoteRepository.java
application/service/NoteApplicationService.java
infrastructure/adapter/in/rest/NoteRestAdapter.java
infrastructure/adapter/in/rest/dto/CreateNoteRequest.java
infrastructure/adapter/in/rest/dto/UpdateNoteRequest.java
infrastructure/adapter/in/rest/dto/NoteResponse.java
infrastructure/adapter/in/rest/mapper/NoteRestMapper.java
infrastructure/adapter/in/rest/exception/RestExceptionHandler.java
infrastructure/adapter/out/persistence/NoteJpaEntity.java
infrastructure/adapter/out/persistence/JpaNoteRepository.java
infrastructure/adapter/out/persistence/mapper/NotePersistenceMapper.java
```

#### Files to Delete (After Migration)

```
entity/Note.java
api/NoteApi.java
resource/NoteResource.java
service/NoteService.java
repository/NoteRepository.java
dto/CreateNoteRequest.java
dto/UpdateNoteRequest.java
dto/NoteResponse.java
```

## Dependency Rule Visualization

```
┌─────────────────────────────────────────────────────────────────────┐
│                        INFRASTRUCTURE                                │
│  ┌─────────────────┐                      ┌─────────────────┐       │
│  │  REST Adapter   │                      │ JPA Repository  │       │
│  │  (JAX-RS)       │                      │ (Panache)       │       │
│  └────────┬────────┘                      └────────┬────────┘       │
│           │ implements                             │ implements     │
│           ▼                                        ▼                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                      APPLICATION                             │   │
│  │   ┌─────────────────┐              ┌─────────────────┐      │   │
│  │   │  Inbound Ports  │◄────────────►│  Outbound Ports │      │   │
│  │   │  (Use Cases)    │              │  (Repository)   │      │   │
│  │   └────────┬────────┘              └────────▲────────┘      │   │
│  │            │ uses                           │ uses          │   │
│  │            ▼                                │               │   │
│  │   ┌─────────────────────────────────────────┴──────────┐   │   │
│  │   │              NoteApplicationService                 │   │   │
│  │   └─────────────────────────┬──────────────────────────┘   │   │
│  └─────────────────────────────┼──────────────────────────────┘   │
│                                │ uses                              │
│                                ▼                                   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                         DOMAIN                               │   │
│  │   ┌──────────┐  ┌──────────┐  ┌────────────────────────┐   │   │
│  │   │   Note   │  │  NoteId  │  │   Domain Exceptions    │   │   │
│  │   │ (Entity) │  │  (VO)    │  │                        │   │   │
│  │   └──────────┘  └──────────┘  └────────────────────────┘   │   │
│  │                    NO FRAMEWORK DEPENDENCIES                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

## Key Benefits After Migration

1. **Testability**: Domain layer is pure Java - test without Quarkus, mocking, or containers
2. **Framework Independence**: Swap Quarkus for Spring/Micronaut with zero domain changes
3. **Database Independence**: Switch PostgreSQL to MongoDB by implementing new adapter
4. **Clear Boundaries**: Each layer has explicit responsibilities
5. **Maintainability**: Changes to REST API don't touch domain; database changes don't touch REST

## Recommended Execution Order

| Step | Action | Risk |
|------|--------|------|
| 1 | Create domain package structure | Low |
| 2 | Create `NoteId` value object | Low |
| 3 | Create pure `Note` domain entity | Low |
| 4 | Create domain exceptions | Low |
| 5 | Create port interfaces | Low |
| 6 | Create `NoteApplicationService` | Medium |
| 7 | Create persistence adapter + mapper | Medium |
| 8 | Create REST adapter + mapper | Medium |
| 9 | Update tests to new structure | Medium |
| 10 | Delete old packages | High |
| 11 | Run full test suite | Validation |
