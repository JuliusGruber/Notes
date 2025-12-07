package com.example.notes.infrastructure.adapter.in.rest.dto;

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
}
