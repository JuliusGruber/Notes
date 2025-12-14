package com.example.notes.infrastructure.adapter.in.rest;

import com.example.notes.domain.exception.NoteNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NoteNotFoundExceptionMapper implements ExceptionMapper<NoteNotFoundException> {

    @Override
    public Response toResponse(NoteNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }

    public record ErrorResponse(String message) {
    }
}
