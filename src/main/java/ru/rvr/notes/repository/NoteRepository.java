package ru.rvr.notes.repository;

import org.springframework.stereotype.Repository;
import ru.rvr.notes.entity.Note;
import ru.rvr.notes.entity.Tag;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class NoteRepository extends AbstractPersistenceRepository<Long,Note> {

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Creates or updates note.
     *
     * @param entity note to create or update
     * @return persisted note
     */
    @Override
    public Note save(Note entity) {
        return super.save(entity);
    }


    /**
     * Find notes by tag
     *
     * @param tag required tag
     * @return notes with specified tag
     */
    public List<Note> getByTag(Tag tag) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Note> query = builder.createQuery(Note.class);
        Root<Note> noteRoot = query.from(Note.class);
        CriteriaQuery<Note> noteCriteriaQuery = query.select(noteRoot).where(builder.isMember(tag, noteRoot.get("tags")));
        return entityManager.createQuery(noteCriteriaQuery).getResultList();
    }


    /**
     * Find notes since specified datetime
     *
     * @param dateTime start datetime
     * @return notes with specified tag
     */
    public List<Note> getSinceDateTime(LocalDateTime dateTime) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Note> query = builder.createQuery(Note.class);
        Root<Note> noteRoot = query.from(Note.class);
        CriteriaQuery<Note> noteCriteriaQuery = query.select(noteRoot).where(builder.greaterThanOrEqualTo(noteRoot.get("createdAt"), dateTime));
        return entityManager.createQuery(noteCriteriaQuery).getResultList();
    }

    /**
     * Find notes with search string in name or content
     *
     * @param search search string
     * @return list of notes
     */
    public List<Note> getBySearch(String search) {
        search = "%" + search + "%";
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Note> query = builder.createQuery(Note.class);
        Root<Note> noteRoot = query.from(Note.class);
        CriteriaQuery<Note> noteCriteriaQuery = query.select(noteRoot).where(builder.or(builder.like(noteRoot.get("name"), search), builder.like(noteRoot.get("content"), search)));
        return entityManager.createQuery(noteCriteriaQuery).getResultList();
    }
}
