package com.example.notes.domain.exception;

public class NoteValidationException extends RuntimeException {

    public NoteValidationException(String message) {
        super(message);
    }
}
