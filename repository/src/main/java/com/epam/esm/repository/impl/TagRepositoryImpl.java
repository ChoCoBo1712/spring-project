package com.epam.esm.repository.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {

  @PersistenceContext private final EntityManager entityManager;

  public TagRepositoryImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<Tag> findAll() {
    return entityManager.createQuery("SELECT tag FROM Tag tag", Tag.class).getResultList();
  }

  @Override
  public Optional<Tag> findById(long id) {
    Tag tag = entityManager.find(Tag.class, id);
    return Optional.ofNullable(tag);
  }

  @Override
  public Optional<Tag> findByName(String name) {
    Tag tag =
        entityManager
            .createQuery("SELECT tag FROM Tag tag WHERE tag.name = ?1", Tag.class)
            .setParameter(1, name)
            .getSingleResult();
    return Optional.ofNullable(tag);
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
