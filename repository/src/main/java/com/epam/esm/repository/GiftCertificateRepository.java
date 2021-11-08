package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;

import java.util.List;
import java.util.Optional;

/**
 * Implementors of this interface provide functionality for manipulating stored {@link
 * GiftCertificate} entities.
 *
 * @author Evgeniy Sokolchik
 */
public interface GiftCertificateRepository {

  /**
   * Retrieve certificates according to specified parameters. All parameters are optional, so if
   * they are not present, all certificates will be retrieved
   *
   * @param tagName tag name (can be partly qualified)
   * @param name certificate name (can be partly qualified)
   * @param description certificate description (can be partly qualified)
   * @param sort String specifying sorting params
   * @return list of {@link GiftCertificate}
   */
  List<GiftCertificate> filter(String tagName, String name, String description, String sort);

  /**
   * Retrieve certificate by its unique id.
   *
   * @param id certificate id
   * @return {@link GiftCertificate} wrapped by {@link Optional}
   */
  Optional<GiftCertificate> findById(long id);

  /**
   * Add relation of existing certificate and tag to many-to-many db table.
   *
   * @param certificateId certificate id
   * @param tagId tag id
   */
  void addCertificateTagRelation(long certificateId, long tagId);

  /**
   * Remove relation of existing certificate and tag from many-to-many db table.
   *
   * @param certificateId certificate id
   * @param tagId tag id
   */
  void removeCertificateTagRelation(long certificateId, long tagId);

  /**
   * Create a new certificate in the storage.
   *
   * @param certificate {@link GiftCertificate} instance
   * @return unique id of the saved {@link GiftCertificate}
   */
  long create(GiftCertificate certificate);

  /**
   * Update an existing certificate in the storage.
   *
   * @param certificate {@link GiftCertificate} instance
   * @return {@code true} if {@link GiftCertificate} existed and was updated, otherwise {@code
   *     false}
   */
  boolean update(GiftCertificate certificate);

  /**
   * Delete an existing certificate from the storage.
   *
   * @param id certificate id
   * @return {@code true} if {@link GiftCertificate} existed and was deleted, otherwise {@code
   *     false}
   */
  boolean delete(long id);
}
