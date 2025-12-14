package com.example.notes.application.port.in;

import com.example.notes.domain.model.Note;

import java.util.List;

public interface ListNotesUseCase {

    List<Note> listNotes();
}
