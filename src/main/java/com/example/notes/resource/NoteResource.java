package com.example.notes.resource;

import com.example.notes.api.NoteApi;
import com.example.notes.entity.Note;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class NoteResource implements NoteApi {

    @Override
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
