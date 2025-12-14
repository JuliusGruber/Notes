package com.example.notes.domain.model;

import com.example.notes.domain.exception.NoteValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Note {

    private final NoteId id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<String> tags;

    private Note(NoteId id, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt, List<String> tags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }

    public static Note create(String title, String content, List<String> tags) {
        validateTitle(title);
        validateContent(content);

        LocalDateTime now = LocalDateTime.now();
        return new Note(
            NoteId.generate(),
            title,
            content,
            now,
            now,
            tags
        );
    }

    public static Note reconstitute(NoteId id, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt, List<String> tags) {
        return new Note(id, title, content, createdAt, updatedAt, tags);
    }

    public Note update(String title, String content, List<String> tags) {
        validateTitle(title);
        validateContent(content);

        return new Note(
            this.id,
            title,
            content,
            this.createdAt,
            LocalDateTime.now(),
            tags
        );
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new NoteValidationException("Title is required");
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new NoteValidationException("Content is required");
        }
    }

    public NoteId id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public List<String> tags() {
        return Collections.unmodifiableList(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
