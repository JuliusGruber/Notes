package com.example.notes.resource;

import com.example.notes.api.NoteApi;
import com.example.notes.dto.CreateNoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.entity.Note;
import com.example.notes.service.NoteService;
import jakarta.ws.rs.core.Response;

public class NoteResource implements NoteApi {

    private final NoteService noteService;

    public NoteResource(NoteService noteService) {
        this.noteService = noteService;
    }

    @Override
    public Response createNote(CreateNoteRequest request) {
        Note note = noteService.createNote(request);
        return Response.status(Response.Status.CREATED).entity(NoteResponse.from(note)).build();
    }
}
