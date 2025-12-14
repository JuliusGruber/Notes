package com.example.notes.infrastructure.adapter.out.persistence.mapper;

import com.example.notes.domain.model.Note;
import com.example.notes.domain.model.NoteId;
import com.example.notes.infrastructure.adapter.out.persistence.NoteJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotePersistenceMapperTest {

    private NotePersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NotePersistenceMapper();
    }

    @Test
    void toNewJpaEntity_withValidNote_createsEntity() {
        Note note = Note.create("Title", "Content", List.of("tag1", "tag2"));

        NoteJpaEntity entity = mapper.toNewJpaEntity(note);

        assertEquals("Title", entity.title);
        assertEquals("Content", entity.content);
        assertEquals(note.createdAt(), entity.createdAt);
        assertEquals(note.updatedAt(), entity.updatedAt);
        assertEquals(List.of("tag1", "tag2"), entity.tags);
    }

    @Test
    void toNewJpaEntity_withNullTags_createsEntityWithEmptyTags() {
        Note note = Note.create("Title", "Content", null);

        NoteJpaEntity entity = mapper.toNewJpaEntity(note);

        assertNotNull(entity.tags);
        assertTrue(entity.tags.isEmpty());
    }

    @Test
    void toNewJpaEntity_doesNotSetId() {
        Note note = Note.create("Title", "Content", List.of());

        NoteJpaEntity entity = mapper.toNewJpaEntity(note);

        assertNull(entity.id);
    }

    @Test
    void toDomainEntity_withValidEntity_createsDomainNote() {
        NoteJpaEntity entity = new NoteJpaEntity();
        entity.id = UUID.randomUUID();
        entity.title = "Title";
        entity.content = "Content";
        entity.createdAt = LocalDateTime.now().minusHours(1);
        entity.updatedAt = LocalDateTime.now();
        entity.tags = List.of("tag1", "tag2");

        Note note = mapper.toDomainEntity(entity);

        assertEquals(entity.id, note.id().value());
        assertEquals("Title", note.title());
        assertEquals("Content", note.content());
        assertEquals(entity.createdAt, note.createdAt());
        assertEquals(entity.updatedAt, note.updatedAt());
        assertEquals(List.of("tag1", "tag2"), note.tags());
    }

    @Test
    void toDomainEntity_preservesTimestampDifference() {
        NoteJpaEntity entity = new NoteJpaEntity();
        entity.id = UUID.randomUUID();
        entity.title = "Title";
        entity.content = "Content";
        entity.createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        entity.updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);
        entity.tags = List.of();

        Note note = mapper.toDomainEntity(entity);

        assertNotEquals(note.createdAt(), note.updatedAt());
        assertEquals(entity.createdAt, note.createdAt());
        assertEquals(entity.updatedAt, note.updatedAt());
    }

    @Test
    void roundTrip_preservesData() {
        Note originalNote = Note.create("Title", "Content", List.of("tag1", "tag2"));

        NoteJpaEntity entity = mapper.toNewJpaEntity(originalNote);
        entity.id = UUID.randomUUID();

        Note reconstitutedNote = mapper.toDomainEntity(entity);

        assertEquals(entity.id, reconstitutedNote.id().value());
        assertEquals(originalNote.title(), reconstitutedNote.title());
        assertEquals(originalNote.content(), reconstitutedNote.content());
        assertEquals(originalNote.createdAt(), reconstitutedNote.createdAt());
        assertEquals(originalNote.updatedAt(), reconstitutedNote.updatedAt());
        assertEquals(originalNote.tags(), reconstitutedNote.tags());
    }
}
