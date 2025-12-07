# Spec: CRUD Quarkus Backend for Note Entity

## Entity: Note

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | UUID | Primary key, auto-generated |
| `title` | String | Not null |
| `content` | String | Not null |
| `createdAt` | LocalDateTime | Not null, auto-set on creation |
| `updatedAt` | LocalDateTime | Not null, auto-updated on modification |
| `tags` | List<String> | Not null, always returns empty list `[]` instead of null |

## Technology Stack

- **Framework**: Quarkus
- **Database**: PostgreSQL
- **Persistence**: Hibernate ORM with Panache
- **Schema Migration**: Flyway
- **API Documentation**: OpenAPI / Swagger UI
- **Testing**: REST Assured

## REST API Endpoints

All API endpoints are versioned with a `/v1` prefix.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/v1/notes` | List all notes |
| `GET` | `/v1/notes/{id}` | Get a single note by ID |
| `POST` | `/v1/notes` | Create a new note |
| `PUT` | `/v1/notes/{id}` | Update an existing note |
| `DELETE` | `/v1/notes/{id}` | Delete a note |

## Features

- OpenAPI/Swagger documentation at `/q/swagger-ui`
- REST Assured integration tests for all endpoints
- Basic not-null validation for required fields
- No pagination or filtering (to keep it simple for initial version)

## Database Schema Migration

Flyway is used for database schema versioning. Migration scripts are located in `src/main/resources/db/migration/` and follow the naming convention `V{version}__{description}.sql`.

| Migration | Description |
|-----------|-------------|
| `V1__Create_notes_table.sql` | Creates the initial `note` table with all fields |

## Architecture

### Interface/Implementation Pattern

This project follows a clean separation between API contracts and implementation:

- **Interface files** (`api/` package): Contain the JAX-RS annotations defining the REST API contract
- **Implementation classes** (`resource/` package): Contain the business logic, implementing the interfaces

| Package | Purpose |
|---------|---------|
| `com.example.notes.api` | API interfaces with JAX-RS annotations (`@Path`, `@GET`, `@POST`, etc.) |
| `com.example.notes.resource` | Implementation classes with business logic |
| `com.example.notes.entity` | JPA entities |

All new endpoints should follow this pattern:
1. Define the method signature with JAX-RS annotations in the interface
2. Implement the business logic in the resource class

## Future Enhancements (out of scope for now)

- Pagination for listing notes
- Filtering/search by title, tags, date range
- Extended validation rules (max lengths, tag constraints)
