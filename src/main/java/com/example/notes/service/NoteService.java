package com.example.notes.service;

import com.example.notes.dto.CreateNoteRequest;
import com.example.notes.entity.Note;
import com.example.notes.repository.NoteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;

@ApplicationScoped
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional
    public Note createNote(CreateNoteRequest request) {
        Note note = new Note();
        note.title = request.title();
        note.content = request.content();
        note.tags = request.tags() != null ? new ArrayList<>(request.tags()) : new ArrayList<>();

        noteRepository.persist(note);
        return note;
    }
}
