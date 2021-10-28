package com.chocobo.esm.repository;

import com.chocobo.esm.entity.GiftCertificate;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateRepository {

    List<GiftCertificate> filter(String tagName, String certificateName, String description,
                                 OrderingType orderByName, OrderingType orderByCreateDate);

    Optional<GiftCertificate> findById(long id);

    void addTag(long certificateId, long tagId);

    void removeTag(long certificateId, long tagId);

    long create(GiftCertificate certificate);

    boolean update(GiftCertificate certificate);

    boolean delete(long id);
}
