# Architecture Overview

This document describes the architecture of the Notes API project.

## Architecture Style: Hexagonal (Ports & Adapters)

The Notes API follows **Hexagonal Architecture**, which isolates business logic from external concerns like HTTP, databases, and messaging. The key principle: **dependencies point inward** - the domain knows nothing about infrastructure.

```
                    ┌─────────────────────────────────────────┐
                    │            INFRASTRUCTURE               │
                    │  ┌─────────────────────────────────────┐│
                    │  │          APPLICATION                ││
                    │  │  ┌─────────────────────────────────┐││
   REST ────────────┼──┼──┤         DOMAIN                  │││
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

## Package Structure

```
src/main/java/com/example/notes/
├── domain/                          # CORE - Zero framework dependencies
│   ├── model/
│   │   ├── Note.java               # Pure domain entity (no JPA)
│   │   └── NoteId.java             # Value object for ID
│   └── exception/
│       ├── NoteNotFoundException.java
│       └── NoteValidationException.java
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
│   │       └── NoteRepository.java
│   └── service/
│       └── NoteApplicationService.java
│
└── infrastructure/                  # ADAPTERS - Framework-specific code
    ├── adapter/
    │   ├── in/rest/                # REST adapter (primary/inbound)
    │   │   ├── NoteRestAdapter.java
    │   │   ├── dto/
    │   │   ├── mapper/
    │   │   └── exception/
    │   └── out/persistence/        # Database adapter (secondary/outbound)
    │       ├── JpaNoteRepository.java
    │       ├── NoteJpaEntity.java
    │       └── mapper/
    └── config/
```

## Layer Responsibilities

### Domain Layer
The innermost layer contains pure business logic with **zero framework dependencies**.

- **Entities**: Core business objects (`Note`)
- **Value Objects**: Immutable objects representing domain concepts (`NoteId`)
- **Domain Exceptions**: Business rule violations (`NoteNotFoundException`, `NoteValidationException`)

### Application Layer
Orchestrates the domain to fulfill use cases.

- **Inbound Ports**: Interfaces defining what the application offers (use cases)
- **Outbound Ports**: Interfaces defining what the application needs (repositories)
- **Application Services**: Implementations that coordinate domain objects

### Infrastructure Layer
Contains all framework-specific code and external integrations.

- **Inbound Adapters**: HTTP controllers, CLI handlers, message consumers
- **Outbound Adapters**: Database repositories, external API clients, message publishers
- **Configuration**: CDI producers, framework setup

## Key Principles

1. **Dependency Rule**: Dependencies always point inward. Domain has no external dependencies.

2. **Ports & Adapters**: Communication between layers happens through interfaces (ports) implemented by adapters.

3. **Framework Independence**: The domain layer can be tested without starting Quarkus or any container.

4. **Database Independence**: Switching databases only requires a new outbound adapter.

## Dependency Flow

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

## Benefits

1. **Testability**: Domain layer is pure Java - test without Quarkus or containers
2. **Framework Independence**: Swap Quarkus for Spring/Micronaut with zero domain changes
3. **Database Independence**: Switch PostgreSQL to MongoDB by implementing a new adapter
4. **Clear Boundaries**: Each layer has explicit responsibilities
5. **Maintainability**: Changes to REST API don't touch domain; database changes don't touch REST
