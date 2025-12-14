package com.example.notes.application.service;

import com.example.notes.application.port.in.CreateNoteUseCase;
import com.example.notes.application.port.in.DeleteNoteUseCase;
import com.example.notes.application.port.out.NoteRepository;
import com.example.notes.domain.exception.NoteNotFoundException;
import com.example.notes.domain.model.Note;
import com.example.notes.domain.model.NoteId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class NoteApplicationService implements CreateNoteUseCase, DeleteNoteUseCase {

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

    @Override
    @Transactional
    public void deleteNote(UUID id) {
        NoteId noteId = NoteId.of(id);
        if (!noteRepository.existsById(noteId)) {
            throw new NoteNotFoundException(id);
        }
        noteRepository.deleteById(noteId);
    }
}
