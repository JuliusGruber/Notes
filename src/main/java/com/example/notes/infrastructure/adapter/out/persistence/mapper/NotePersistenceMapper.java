package com.example.notes.infrastructure.adapter.out.persistence.mapper;

import com.example.notes.domain.model.Note;
import com.example.notes.domain.model.NoteId;
import com.example.notes.infrastructure.adapter.out.persistence.NoteJpaEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotePersistenceMapper {

    public NoteJpaEntity toNewJpaEntity(Note note) {
        NoteJpaEntity entity = new NoteJpaEntity();
        entity.title = note.title();
        entity.content = note.content();
        entity.createdAt = note.createdAt();
        entity.updatedAt = note.updatedAt();
        entity.tags = note.tags();
        return entity;
    }

    public Note toDomainEntity(NoteJpaEntity entity) {
        return Note.reconstitute(
            NoteId.of(entity.id),
            entity.title,
            entity.content,
            entity.createdAt,
            entity.updatedAt,
            entity.tags
        );
    }
}
