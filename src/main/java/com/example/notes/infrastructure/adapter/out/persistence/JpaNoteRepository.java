package com.example.notes.infrastructure.adapter.out.persistence;

import com.example.notes.application.port.out.NoteRepository;
import com.example.notes.domain.model.Note;
import com.example.notes.domain.model.NoteId;
import com.example.notes.infrastructure.adapter.out.persistence.mapper.NotePersistenceMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JpaNoteRepository implements NoteRepository {

    private final NotePersistenceMapper mapper;

    public JpaNoteRepository(NotePersistenceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Note save(Note note) {
        NoteJpaEntity existingEntity = NoteJpaEntity.findById(note.id().value());
        if (existingEntity != null) {
            mapper.updateJpaEntity(existingEntity, note);
            return mapper.toDomainEntity(existingEntity);
        }
        NoteJpaEntity entity = mapper.toNewJpaEntity(note);
        entity.persist();
        return mapper.toDomainEntity(entity);
    }

    @Override
    public Optional<Note> findById(NoteId id) {
        return NoteJpaEntity.<NoteJpaEntity>findByIdOptional(id.value())
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<Note> findAll() {
        return NoteJpaEntity.<NoteJpaEntity>listAll()
                .stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public boolean existsById(NoteId id) {
        return NoteJpaEntity.findByIdOptional(id.value()).isPresent();
    }

    @Override
    public void deleteById(NoteId id) {
        NoteJpaEntity.deleteById(id.value());
    }
}
