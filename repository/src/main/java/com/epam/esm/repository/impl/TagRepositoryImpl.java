package com.epam.esm.repository.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {

  @PersistenceContext private final EntityManager entityManager;

  public TagRepositoryImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<Tag> findAll(int page, int pageSize) {
    TypedQuery<Tag> query =
        entityManager
            .createQuery("SELECT tag FROM Tag tag", Tag.class)
            .setFirstResult(pageSize * (page - 1))
            .setMaxResults(pageSize);
    return query.getResultList();
  }

  @Override
  public Optional<Tag> findById(long id) {
    Tag tag = entityManager.find(Tag.class, id);
    return Optional.ofNullable(tag);
  }

  @Override
  public Optional<Tag> findByName(String name) {
    return entityManager
        .createQuery("SELECT tag FROM Tag tag WHERE tag.name = ?1", Tag.class)
        .setParameter(1, name)
        .getResultStream()
        .findFirst();
  }

  @Override
  public Tag create(Tag tag) {
    entityManager.persist(tag);
    return tag;
  }

  @Override
  public void delete(Tag tag) {
    entityManager.remove(tag);
  }
}
