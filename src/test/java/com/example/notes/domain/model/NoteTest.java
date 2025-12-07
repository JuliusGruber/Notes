package com.example.notes.domain.model;

import com.example.notes.domain.exception.NoteValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteTest {

    @Test
    void createNote_withValidData_createsNote() {
        Note note = Note.create("Test Title", "Test Content", List.of("tag1", "tag2"));

        assertNotNull(note.id());
        assertEquals("Test Title", note.title());
        assertEquals("Test Content", note.content());
        assertNotNull(note.createdAt());
        assertNotNull(note.updatedAt());
        assertEquals(List.of("tag1", "tag2"), note.tags());
    }

    @Test
    void createNote_withNullTags_createsNoteWithEmptyTags() {
        Note note = Note.create("Test Title", "Test Content", null);

        assertNotNull(note.tags());
        assertTrue(note.tags().isEmpty());
    }

    @Test
    void createNote_withNullTitle_throwsValidationException() {
        NoteValidationException exception = assertThrows(
            NoteValidationException.class,
            () -> Note.create(null, "Content", null)
        );
        assertEquals("Title is required", exception.getMessage());
    }

    @Test
    void createNote_withBlankTitle_throwsValidationException() {
        NoteValidationException exception = assertThrows(
            NoteValidationException.class,
            () -> Note.create("   ", "Content", null)
        );
        assertEquals("Title is required", exception.getMessage());
    }

    @Test
    void createNote_withNullContent_throwsValidationException() {
        NoteValidationException exception = assertThrows(
            NoteValidationException.class,
            () -> Note.create("Title", null, null)
        );
        assertEquals("Content is required", exception.getMessage());
    }

    @Test
    void createNote_withBlankContent_throwsValidationException() {
        NoteValidationException exception = assertThrows(
            NoteValidationException.class,
            () -> Note.create("Title", "   ", null)
        );
        assertEquals("Content is required", exception.getMessage());
    }
}
