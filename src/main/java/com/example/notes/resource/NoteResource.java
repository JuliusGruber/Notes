package com.example.notes.resource;

import com.example.notes.entity.Note;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Path("/v1/notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NoteResource {

    @POST
    public Response createNote(Note request) {
        Note note = new Note();
        note.id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        note.title = request != null && request.title != null ? request.title : "Dummy Note";
        note.content = request != null && request.content != null ? request.content : "Dummy content";
        LocalDateTime now = LocalDateTime.now();
        note.createdAt = now;
        note.updatedAt = now;
        note.tags = new ArrayList<>();

        return Response.status(Response.Status.CREATED).entity(note).build();
    }
}
