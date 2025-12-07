package com.example.notes.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NoteIdTest {

    @Test
    void generate_createsUniqueId() {
        NoteId id1 = NoteId.generate();
        NoteId id2 = NoteId.generate();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    void of_withValidUuid_createsNoteId() {
        UUID uuid = UUID.randomUUID();
        NoteId noteId = NoteId.of(uuid);

        assertEquals(uuid, noteId.value());
    }

    @Test
    void of_withNullUuid_throwsException() {
        assertThrows(NullPointerException.class, () -> NoteId.of(null));
    }

    @Test
    void equals_withSameValue_returnsTrue() {
        UUID uuid = UUID.randomUUID();
        NoteId id1 = NoteId.of(uuid);
        NoteId id2 = NoteId.of(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void toString_returnsUuidString() {
        UUID uuid = UUID.randomUUID();
        NoteId noteId = NoteId.of(uuid);

        assertEquals(uuid.toString(), noteId.toString());
    }
}
