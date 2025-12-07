package com.example.notes.service;

import com.example.notes.dto.CreateNoteRequest;
import com.example.notes.entity.Note;
import com.example.notes.repository.NoteRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class NoteServiceTest {

    @Inject
    NoteService noteService;

    @Inject
    NoteRepository noteRepository;

    @Test
    public void testCreateNotePersistsToDatabase() {
        CreateNoteRequest request = new CreateNoteRequest("Test Title", "Test Content", List.of("tag1", "tag2"));

        Note createdNote = noteService.createNote(request);

        assertNotNull(createdNote.id);
        assertEquals("Test Title", createdNote.title);
        assertEquals("Test Content", createdNote.content);
        assertEquals(List.of("tag1", "tag2"), createdNote.tags);
        assertNotNull(createdNote.createdAt);
        assertNotNull(createdNote.updatedAt);

        Note foundNote = noteRepository.findById(createdNote.id);
        assertNotNull(foundNote);
        assertEquals(createdNote.id, foundNote.id);
        assertEquals("Test Title", foundNote.title);
        assertEquals("Test Content", foundNote.content);
    }

    @Test
    public void testCreateNoteWithNullTagsUsesEmptyList() {
        CreateNoteRequest request = new CreateNoteRequest("Test Title", "Test Content", null);

        Note createdNote = noteService.createNote(request);

        assertNotNull(createdNote.tags);
        assertTrue(createdNote.tags.isEmpty());
    }

    @Test
    public void testCreateNoteWithEmptyTagsList() {
        CreateNoteRequest request = new CreateNoteRequest("Test Title", "Test Content", List.of());

        Note createdNote = noteService.createNote(request);

        assertNotNull(createdNote.tags);
        assertTrue(createdNote.tags.isEmpty());
    }
}
