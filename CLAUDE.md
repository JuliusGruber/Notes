# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Notes API is a CRUD Quarkus backend for managing Note entities. It uses PostgreSQL for production and H2 for testing.

## Build and Development Commands

```bash
# Run in development mode with hot reload
./mvnw quarkus:dev

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=NoteResourceTest

# Run a single test method
./mvnw test -Dtest=NoteResourceTest#testGetAllNotes

# Build the project
./mvnw package

# Build native executable
./mvnw package -Dnative
```

## Tech Stack

- **Java 17** with Quarkus 3.17.2
- **Hibernate ORM with Panache** - Active Record pattern for entities
- **Flyway** - Database migrations in `src/main/resources/db/migration/`
- **REST Assured** - Testing framework
- **H2** - In-memory database for tests (Hibernate auto-generates schema)

## Architecture

This is a simple CRUD API with a single entity:

- **Entity**: `Note` (`src/main/java/com/example/notes/entity/Note.java`) - Uses Panache's `PanacheEntity` which provides the `id` field and repository methods
- **REST Endpoints**: `/notes` for CRUD operations
- **API Docs**: Swagger UI at `/q/swagger-ui`

## Database Configuration

- **Production**: PostgreSQL on `localhost:5432/notes`, Flyway runs migrations at startup
- **Tests**: H2 in-memory, Flyway disabled, Hibernate generates schema via `drop-and-create`

## API Specification

See `specs/SPEC.md` for the complete API specification including all endpoints and the Note entity schema.

## Git Workflow

This project strictly uses **rebase merging** for pull requests on GitHub. Do not use merge commits or squash merging.
