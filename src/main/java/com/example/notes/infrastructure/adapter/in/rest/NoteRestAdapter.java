package com.example.notes.infrastructure.adapter.in.rest;

import com.example.notes.application.port.in.CreateNoteUseCase;
import com.example.notes.domain.model.Note;
import com.example.notes.infrastructure.adapter.in.rest.dto.CreateNoteRequest;
import com.example.notes.infrastructure.adapter.in.rest.dto.NoteResponse;
import com.example.notes.infrastructure.adapter.in.rest.mapper.NoteRestMapper;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1/notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NoteRestAdapter {

    private final CreateNoteUseCase createNoteUseCase;
    private final NoteRestMapper mapper;

    public NoteRestAdapter(CreateNoteUseCase createNoteUseCase, NoteRestMapper mapper) {
        this.createNoteUseCase = createNoteUseCase;
        this.mapper = mapper;
    }

    @POST
    public Response createNote(@Valid CreateNoteRequest request) {
        Note note = createNoteUseCase.createNote(mapper.toCommand(request));
        NoteResponse response = mapper.toResponse(note);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
