package com.example.notes.service;

import com.example.notes.dto.CreateNoteRequest;
import com.example.notes.entity.Note;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@ApplicationScoped
public class NoteService {

    public Note createNote(CreateNoteRequest request) {
        Note note = new Note();
        note.id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        note.title = request.title();
        note.content = request.content();
        LocalDateTime now = LocalDateTime.now();
        note.createdAt = now;
        note.updatedAt = now;
        note.tags = request.tags() != null ? new ArrayList<>(request.tags()) : new ArrayList<>();

        return note;
    }
}
