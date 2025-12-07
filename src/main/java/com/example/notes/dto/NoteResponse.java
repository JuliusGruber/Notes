package com.example.notes.dto;

import com.example.notes.entity.Note;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NoteResponse(
    UUID id,
    String title,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<String> tags
) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(
            note.id,
            note.title,
            note.content,
            note.createdAt,
            note.updatedAt,
            note.tags
        );
    }
}
