package com.example.notes.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateNoteRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Content is required")
    String content,

    List<String> tags
) {
}
