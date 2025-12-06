# Spec: CRUD Quarkus Backend for Note Entity

## Entity: Note

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | Long | Primary key, auto-generated |
| `title` | String | Not null |
| `content` | String | Not null |
| `createdAt` | LocalDateTime | Not null, auto-set on creation |
| `updatedAt` | LocalDateTime | Not null, auto-updated on modification |
| `tags` | List<String> | Not null (can be empty list) |

## Technology Stack

- **Framework**: Quarkus
- **Database**: PostgreSQL
- **Persistence**: Hibernate ORM with Panache
- **API Documentation**: OpenAPI / Swagger UI
- **Testing**: REST Assured

## REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/notes` | List all notes |
| `GET` | `/notes/{id}` | Get a single note by ID |
| `POST` | `/notes` | Create a new note |
| `PUT` | `/notes/{id}` | Update an existing note |
| `DELETE` | `/notes/{id}` | Delete a note |

## Features

- OpenAPI/Swagger documentation at `/q/swagger-ui`
- REST Assured integration tests for all endpoints
- Basic not-null validation for required fields
- No pagination or filtering (to keep it simple for initial version)

## Future Enhancements (out of scope for now)

- Pagination for listing notes
- Filtering/search by title, tags, date range
- Extended validation rules (max lengths, tag constraints)
