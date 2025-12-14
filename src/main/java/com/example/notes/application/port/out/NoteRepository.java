package com.example.notes.application.port.out;

import com.example.notes.domain.model.Note;
import com.example.notes.domain.model.NoteId;

public interface NoteRepository {

    Note save(Note note);

    boolean existsById(NoteId id);

    void deleteById(NoteId id);
}
