package com.example.notes.infrastructure.adapter.out.persistence;

import com.example.notes.application.port.out.NoteRepository;
import com.example.notes.domain.model.Note;
import com.example.notes.infrastructure.adapter.out.persistence.mapper.NotePersistenceMapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JpaNoteRepository implements NoteRepository {

    private final NotePersistenceMapper mapper;

    public JpaNoteRepository(NotePersistenceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Note save(Note note) {
        NoteJpaEntity entity = mapper.toNewJpaEntity(note);
        entity.persist();
        return mapper.toDomainEntity(entity);
    }
}
