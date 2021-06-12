package ru.rvr.notes.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.rvr.notes.entity.Tag;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
@Slf4j
public class TagRepository extends AbstractPersistenceRepository<Long,Tag> {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates or updates entity.
     *
     * @param entity tag to create or update
     * @return persisted tag
     */
    @Override
    public Tag save(Tag entity) {
        return super.save(entity);
    }

    /**
     * Get persisted entity by id or name.
     * If no such entity creates it.
     *
     * @param entity not persisted entity
     * @return persisted entity
     */
    public Tag getOrCreate(Tag entity) {
        Tag loaded;
        if (entity.getId() != null) {
            loaded = getById(entity.getId());
            if (loaded != null) {
                return loaded;
            }
        }
        loaded = getByName(entity.getName());
        if (loaded != null){
            return loaded;
        }
        return save(entity);
    }

    /**
     * Find tag by name
     *
     * @param name full name of tag
     * @return persisted tag or null
     */
    public Tag getByName(String name) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> query = builder.createQuery(Tag.class);
        Root<Tag> noteRoot = query.from(Tag.class);
        CriteriaQuery<Tag> noteCriteriaQuery = query.select(noteRoot).where(builder.equal(noteRoot.get("name"),name));
        List<Tag> tags = entityManager.createQuery(noteCriteriaQuery).setMaxResults(1).getResultList();
        if (tags.size() != 1) {
            // Due to constraints assuming that we have either 0 or 1 tag with this name and return null
            return null;
        }
        return tags.get(0);
    }


    /**
     * For each task in list either loads or creates persisted tag
     *
     * @param tags list of tags with id and/or name
     * @return list of persisted tags
     */
    public List<Tag> getPersistedTagsFromList(List<Tag> tags){
        return tags.stream().map(this::getOrCreate).collect(Collectors.toList());
    }
}
