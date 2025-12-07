package com.example.notes.application.service;

import com.example.notes.application.port.in.CreateNoteUseCase;
import com.example.notes.application.port.out.NoteRepository;
import com.example.notes.domain.model.Note;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class NoteApplicationService implements CreateNoteUseCase {

    private final NoteRepository noteRepository;

    public NoteApplicationService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    @Transactional
    public Note createNote(CreateNoteCommand command) {
        Note note = Note.create(command.title(), command.content(), command.tags());
        return noteRepository.save(note);
    }
}
