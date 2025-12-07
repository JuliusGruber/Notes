package com.example.notes.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
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
            .post("/notes")
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
    public void testCreateNoteWithEmptyBody() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/notes")
        .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("title", is("Dummy Note"))
            .body("content", is("Dummy content"));
    }
}
