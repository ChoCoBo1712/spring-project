package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementors of the interface provide functionality for manipulating stored {@link Tag} entities.
 *
 * @author Evgeniy Sokolchik
 */
public interface TagRepository {

  /**
   * Retrieve all tags from storage.
   *
   * @return list of {@link Tag}
   */
  List<Tag> findAll();

  /**
   * Retrieve tag by its unique id.
   *
   * @param id tag id
   * @return {@link Tag} wrapped by {@link Optional}
   */
  Optional<Tag> findById(long id);

  /**
   * Retrieve tag by its unique name.
   *
   * @param name tag name
   * @return {@link Tag} wrapped by {@link Optional}
   */
  Optional<Tag> findByName(String name);

  /**
   * Create a new tag in the storage.
   *
   * @param tag {@link Tag} instance
   * @return {@link Tag} instance
   */
  Tag create(Tag tag);

  /**
   * Delete an existing tag from the storage.
   *
   * @param tag {@link Tag} instance
   */
  void delete(Tag tag);
}
