package com.example.notes.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateNoteRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Content is required")
    String content,

    List<String> tags
) {
}
