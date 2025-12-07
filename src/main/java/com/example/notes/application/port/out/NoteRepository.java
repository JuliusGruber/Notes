package com.example.notes.application.port.out;

import com.example.notes.domain.model.Note;

public interface NoteRepository {

    Note save(Note note);
}
