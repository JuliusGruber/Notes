package com.example.notes.application.service;

import com.example.notes.application.port.out.NoteRepository;
import com.example.notes.domain.exception.NoteNotFoundException;
import com.example.notes.domain.model.NoteId;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class NoteApplicationServiceDeleteTest {

    @Inject
    NoteApplicationService service;

    @InjectMock
    NoteRepository noteRepository;

    @Test
    void deleteNote_whenNoteExists_deletesNote() {
        UUID noteId = UUID.randomUUID();
        when(noteRepository.existsById(any(NoteId.class))).thenReturn(true);

        service.deleteNote(noteId);

        ArgumentCaptor<NoteId> idCaptor = ArgumentCaptor.forClass(NoteId.class);
        verify(noteRepository).deleteById(idCaptor.capture());
        assertEquals(noteId, idCaptor.getValue().value());
    }

    @Test
    void deleteNote_whenNoteDoesNotExist_throwsNoteNotFoundException() {
        UUID noteId = UUID.randomUUID();
        when(noteRepository.existsById(any(NoteId.class))).thenReturn(false);

        NoteNotFoundException exception = assertThrows(
            NoteNotFoundException.class,
            () -> service.deleteNote(noteId)
        );

        assertEquals("Note not found with id: " + noteId, exception.getMessage());
        verify(noteRepository, never()).deleteById(any(NoteId.class));
    }

    @Test
    void deleteNote_checksExistenceBeforeDeleting() {
        UUID noteId = UUID.randomUUID();
        when(noteRepository.existsById(any(NoteId.class))).thenReturn(true);

        service.deleteNote(noteId);

        var inOrder = inOrder(noteRepository);
        inOrder.verify(noteRepository).existsById(any(NoteId.class));
        inOrder.verify(noteRepository).deleteById(any(NoteId.class));
    }
}
