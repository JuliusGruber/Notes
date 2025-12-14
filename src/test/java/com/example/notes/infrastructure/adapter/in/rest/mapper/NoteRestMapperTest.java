package com.example.notes.infrastructure.adapter.in.rest.mapper;

import com.example.notes.application.port.in.CreateNoteUseCase.CreateNoteCommand;
import com.example.notes.domain.model.Note;
import com.example.notes.infrastructure.adapter.in.rest.dto.CreateNoteRequest;
import com.example.notes.infrastructure.adapter.in.rest.dto.NoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoteRestMapperTest {

    private NoteRestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NoteRestMapper();
    }

    @Test
    void toCommand_withValidRequest_createsCommand() {
        CreateNoteRequest request = new CreateNoteRequest("Title", "Content", List.of("tag1", "tag2"));

        CreateNoteCommand command = mapper.toCommand(request);

        assertEquals("Title", command.title());
        assertEquals("Content", command.content());
        assertEquals(List.of("tag1", "tag2"), command.tags());
    }

    @Test
    void toCommand_withNullTags_preservesNullTags() {
        CreateNoteRequest request = new CreateNoteRequest("Title", "Content", null);

        CreateNoteCommand command = mapper.toCommand(request);

        assertEquals("Title", command.title());
        assertEquals("Content", command.content());
        assertNull(command.tags());
    }

    @Test
    void toCommand_withEmptyTags_preservesEmptyTags() {
        CreateNoteRequest request = new CreateNoteRequest("Title", "Content", List.of());

        CreateNoteCommand command = mapper.toCommand(request);

        assertEquals("Title", command.title());
        assertEquals("Content", command.content());
        assertTrue(command.tags().isEmpty());
    }

    @Test
    void toResponse_withValidNote_createsResponse() {
        Note note = Note.create("Title", "Content", List.of("tag1", "tag2"));

        NoteResponse response = mapper.toResponse(note);

        assertEquals(note.id().value(), response.id());
        assertEquals("Title", response.title());
        assertEquals("Content", response.content());
        assertEquals(note.createdAt(), response.createdAt());
        assertEquals(note.updatedAt(), response.updatedAt());
        assertEquals(List.of("tag1", "tag2"), response.tags());
    }

    @Test
    void toResponse_withNullTags_returnsEmptyTagsList() {
        Note note = Note.create("Title", "Content", null);

        NoteResponse response = mapper.toResponse(note);

        assertNotNull(response.tags());
        assertTrue(response.tags().isEmpty());
    }

    @Test
    void toResponse_preservesUuidFromNote() {
        Note note = Note.create("Title", "Content", List.of());

        NoteResponse response = mapper.toResponse(note);

        assertNotNull(response.id());
        assertEquals(note.id().value(), response.id());
    }

    @Test
    void toResponse_preservesTimestampsFromNote() {
        Note note = Note.create("Title", "Content", List.of());

        NoteResponse response = mapper.toResponse(note);

        assertEquals(note.createdAt(), response.createdAt());
        assertEquals(note.updatedAt(), response.updatedAt());
        assertEquals(response.createdAt(), response.updatedAt());
    }
}
