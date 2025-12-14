package com.example.notes.application.service;

import com.example.notes.application.port.in.CreateNoteUseCase.CreateNoteCommand;
import com.example.notes.application.port.out.NoteRepository;
import com.example.notes.domain.model.Note;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class NoteApplicationServiceTest {

    @Inject
    NoteApplicationService service;

    @InjectMock
    NoteRepository noteRepository;

    @Test
    void createNote_withValidCommand_savesNoteToRepository() {
        CreateNoteCommand command = new CreateNoteCommand("Title", "Content", List.of("tag1", "tag2"));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note result = service.createNote(command);

        assertNotNull(result);
        assertEquals("Title", result.title());
        assertEquals("Content", result.content());
        assertEquals(List.of("tag1", "tag2"), result.tags());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void createNote_passesCorrectNoteToRepository() {
        CreateNoteCommand command = new CreateNoteCommand("Title", "Content", List.of("tag1"));
        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createNote(command);

        verify(noteRepository).save(noteCaptor.capture());
        Note savedNote = noteCaptor.getValue();
        assertEquals("Title", savedNote.title());
        assertEquals("Content", savedNote.content());
        assertEquals(List.of("tag1"), savedNote.tags());
    }

    @Test
    void createNote_withNullTags_createsNoteWithEmptyTags() {
        CreateNoteCommand command = new CreateNoteCommand("Title", "Content", null);
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note result = service.createNote(command);

        assertNotNull(result.tags());
        assertTrue(result.tags().isEmpty());
    }

    @Test
    void createNote_generatesUniqueIdForEachCall() {
        CreateNoteCommand command = new CreateNoteCommand("Title", "Content", List.of());
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note result1 = service.createNote(command);
        Note result2 = service.createNote(command);

        assertNotEquals(result1.id(), result2.id());
    }

    @Test
    void createNote_setsCreatedAtAndUpdatedAtToSameValue() {
        CreateNoteCommand command = new CreateNoteCommand("Title", "Content", List.of());
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note result = service.createNote(command);

        assertEquals(result.createdAt(), result.updatedAt());
    }

    @Test
    void createNote_returnsNoteSavedByRepository() {
        CreateNoteCommand command = new CreateNoteCommand("Title", "Content", List.of());
        Note savedNote = Note.create("Title", "Content", List.of());
        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        Note result = service.createNote(command);

        assertEquals(savedNote, result);
    }
}
