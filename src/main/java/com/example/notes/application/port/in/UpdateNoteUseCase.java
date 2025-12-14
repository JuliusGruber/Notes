package com.example.notes.application.port.in;

import com.example.notes.domain.model.Note;

import java.util.List;
import java.util.UUID;

public interface UpdateNoteUseCase {

    Note updateNote(UUID id, UpdateNoteCommand command);

    record UpdateNoteCommand(String title, String content, List<String> tags) {
    }
}
