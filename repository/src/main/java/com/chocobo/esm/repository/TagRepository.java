package com.chocobo.esm.repository;

import com.chocobo.esm.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    List<Tag> findAll();

    Optional<Tag> findById(long id);

    Optional<Tag> findByName(String name);

    List<Tag> findByCertificateId(long certificateId);

    long create(Tag tag);

    boolean delete(long id);
}
