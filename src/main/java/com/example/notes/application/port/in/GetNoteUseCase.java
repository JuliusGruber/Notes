package com.example.notes.application.port.in;

import com.example.notes.domain.model.Note;

import java.util.UUID;

public interface GetNoteUseCase {

    Note getNote(UUID id);
}
