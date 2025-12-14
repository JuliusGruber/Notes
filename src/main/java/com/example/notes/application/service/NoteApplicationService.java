package com.example.notes.application.service;

import com.example.notes.application.port.in.CreateNoteUseCase;
import com.example.notes.application.port.in.DeleteNoteUseCase;
import com.example.notes.application.port.in.GetNoteUseCase;
import com.example.notes.application.port.in.ListNotesUseCase;
import com.example.notes.application.port.in.UpdateNoteUseCase;
import com.example.notes.application.port.out.NoteRepository;
import com.example.notes.domain.exception.NoteNotFoundException;
import com.example.notes.domain.model.Note;
import com.example.notes.domain.model.NoteId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NoteApplicationService implements CreateNoteUseCase, DeleteNoteUseCase, GetNoteUseCase, ListNotesUseCase, UpdateNoteUseCase {

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
    public Note getNote(UUID id) {
        NoteId noteId = NoteId.of(id);
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(id));
    }

    @Override
    @Transactional
    public List<Note> listNotes() {
        return noteRepository.findAll();
    }

    @Override
    @Transactional
    public Note updateNote(UUID id, UpdateNoteCommand command) {
        NoteId noteId = NoteId.of(id);
        Note existingNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(id));

        Note updatedNote = existingNote.update(command.title(), command.content(), command.tags());
        return noteRepository.save(updatedNote);
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
