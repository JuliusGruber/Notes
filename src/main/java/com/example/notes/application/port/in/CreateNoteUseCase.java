package com.example.notes.application.port.in;

import com.example.notes.domain.model.Note;

import java.util.List;

public interface CreateNoteUseCase {

    Note createNote(CreateNoteCommand command);

    record CreateNoteCommand(String title, String content, List<String> tags) {
    }
}
