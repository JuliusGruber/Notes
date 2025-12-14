package com.example.notes.application.port.in;

import java.util.UUID;

public interface DeleteNoteUseCase {

    void deleteNote(UUID id);
}
