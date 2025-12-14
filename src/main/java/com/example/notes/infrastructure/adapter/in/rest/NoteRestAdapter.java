package com.example.notes.infrastructure.adapter.in.rest;

import com.example.notes.application.port.in.CreateNoteUseCase;
import com.example.notes.application.port.in.DeleteNoteUseCase;
import com.example.notes.application.port.in.GetNoteUseCase;
import com.example.notes.application.port.in.ListNotesUseCase;
import com.example.notes.application.port.in.UpdateNoteUseCase;
import com.example.notes.domain.model.Note;
import com.example.notes.infrastructure.adapter.in.rest.dto.CreateNoteRequest;
import com.example.notes.infrastructure.adapter.in.rest.dto.NoteResponse;
import com.example.notes.infrastructure.adapter.in.rest.dto.UpdateNoteRequest;
import com.example.notes.infrastructure.adapter.in.rest.mapper.NoteRestMapper;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/v1/notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NoteRestAdapter {

    private final CreateNoteUseCase createNoteUseCase;
    private final DeleteNoteUseCase deleteNoteUseCase;
    private final GetNoteUseCase getNoteUseCase;
    private final ListNotesUseCase listNotesUseCase;
    private final UpdateNoteUseCase updateNoteUseCase;
    private final NoteRestMapper mapper;

    public NoteRestAdapter(
            CreateNoteUseCase createNoteUseCase,
            DeleteNoteUseCase deleteNoteUseCase,
            GetNoteUseCase getNoteUseCase,
            ListNotesUseCase listNotesUseCase,
            UpdateNoteUseCase updateNoteUseCase,
            NoteRestMapper mapper) {
        this.createNoteUseCase = createNoteUseCase;
        this.deleteNoteUseCase = deleteNoteUseCase;
        this.getNoteUseCase = getNoteUseCase;
        this.listNotesUseCase = listNotesUseCase;
        this.updateNoteUseCase = updateNoteUseCase;
        this.mapper = mapper;
    }

    @POST
    public Response createNote(@Valid CreateNoteRequest request) {
        Note note = createNoteUseCase.createNote(mapper.toCommand(request));
        NoteResponse response = mapper.toResponse(note);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}")
    public Response getNoteById(@PathParam("id") UUID id) {
        Note note = getNoteUseCase.getNote(id);
        NoteResponse response = mapper.toResponse(note);
        return Response.ok(response).build();
    }

    @GET
    public Response listNotes() {
        List<Note> notes = listNotesUseCase.listNotes();
        List<NoteResponse> response = notes.stream()
                .map(mapper::toResponse)
                .toList();
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateNote(@PathParam("id") UUID id, @Valid UpdateNoteRequest request) {
        Note note = updateNoteUseCase.updateNote(id, mapper.toCommand(request));
        NoteResponse response = mapper.toResponse(note);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteNote(@PathParam("id") UUID id) {
        deleteNoteUseCase.deleteNote(id);
        return Response.noContent().build();
    }
}
