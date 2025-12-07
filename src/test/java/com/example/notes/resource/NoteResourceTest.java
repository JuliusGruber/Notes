package com.example.notes.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class NoteResourceTest {

    @Test
    public void testCreateNote() {
        String requestBody = "{\"title\": \"Test\", \"content\": \"Content\", \"tags\": []}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("title", is("Test"))
            .body("content", is("Content"))
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue())
            .body("tags", notNullValue());
    }

    @Test
    public void testCreateNoteWithEmptyBodyReturnsBadRequest() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(400);
    }

    @Test
    public void testCreateNoteWithNullTitleReturnsBadRequest() {
        String requestBody = "{\"content\": \"Content\", \"tags\": []}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(400)
            .body("violations.field", hasItem("createNote.request.title"))
            .body("violations.message", hasItem("Title is required"));
    }

    @Test
    public void testCreateNoteWithEmptyTitleReturnsBadRequest() {
        String requestBody = "{\"title\": \"\", \"content\": \"Content\", \"tags\": []}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(400)
            .body("violations.field", hasItem("createNote.request.title"))
            .body("violations.message", hasItem("Title is required"));
    }

    @Test
    public void testCreateNoteWithBlankTitleReturnsBadRequest() {
        String requestBody = "{\"title\": \"   \", \"content\": \"Content\", \"tags\": []}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(400)
            .body("violations.field", hasItem("createNote.request.title"))
            .body("violations.message", hasItem("Title is required"));
    }

    @Test
    public void testCreateNoteWithNullContentReturnsBadRequest() {
        String requestBody = "{\"title\": \"Test\", \"tags\": []}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(400)
            .body("violations.field", hasItem("createNote.request.content"))
            .body("violations.message", hasItem("Content is required"));
    }

    @Test
    public void testCreateNoteWithEmptyContentReturnsBadRequest() {
        String requestBody = "{\"title\": \"Test\", \"content\": \"\", \"tags\": []}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(400)
            .body("violations.field", hasItem("createNote.request.content"))
            .body("violations.message", hasItem("Content is required"));
    }

    @Test
    public void testCreateNoteWithBlankContentReturnsBadRequest() {
        String requestBody = "{\"title\": \"Test\", \"content\": \"   \", \"tags\": []}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(400)
            .body("violations.field", hasItem("createNote.request.content"))
            .body("violations.message", hasItem("Content is required"));
    }

    @Test
    public void testCreateNoteWithTags() {
        String requestBody = "{\"title\": \"Test\", \"content\": \"Content\", \"tags\": [\"tag1\", \"tag2\"]}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("tags", hasItem("tag1"))
            .body("tags", hasItem("tag2"));
    }

    @Test
    public void testCreateNoteWithoutTagsUsesEmptyList() {
        String requestBody = "{\"title\": \"Test\", \"content\": \"Content\"}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/v1/notes")
        .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("tags.size()", is(0));
    }
}
