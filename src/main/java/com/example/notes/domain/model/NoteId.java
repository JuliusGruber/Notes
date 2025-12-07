package com.example.notes.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class NoteId {

    private final UUID value;

    private NoteId(UUID value) {
        this.value = Objects.requireNonNull(value, "NoteId value cannot be null");
    }

    public static NoteId of(UUID value) {
        return new NoteId(value);
    }

    public static NoteId generate() {
        return new NoteId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteId noteId = (NoteId) o;
        return Objects.equals(value, noteId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
