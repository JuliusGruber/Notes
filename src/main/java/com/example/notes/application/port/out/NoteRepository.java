package com.example.notes.application.port.out;

import com.example.notes.domain.model.Note;
import com.example.notes.domain.model.NoteId;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {

    Note save(Note note);

    Optional<Note> findById(NoteId id);

    List<Note> findAll();

    boolean existsById(NoteId id);

    void deleteById(NoteId id);
}
