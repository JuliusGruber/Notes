package com.example.notes.infrastructure.adapter.in.rest.mapper;

import com.example.notes.application.port.in.CreateNoteUseCase.CreateNoteCommand;
import com.example.notes.domain.model.Note;
import com.example.notes.infrastructure.adapter.in.rest.dto.CreateNoteRequest;
import com.example.notes.infrastructure.adapter.in.rest.dto.NoteResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NoteRestMapper {

    public CreateNoteCommand toCommand(CreateNoteRequest request) {
        return new CreateNoteCommand(
            request.title(),
            request.content(),
            request.tags()
        );
    }

    public NoteResponse toResponse(Note note) {
        return new NoteResponse(
            note.id().value(),
            note.title(),
            note.content(),
            note.createdAt(),
            note.updatedAt(),
            note.tags()
        );
    }
}
