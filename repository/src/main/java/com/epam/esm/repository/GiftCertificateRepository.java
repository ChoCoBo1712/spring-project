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
   * @param tagNames tag name array (can be partly qualified)
   * @param name certificate name (can be partly qualified)
   * @param description certificate description (can be partly qualified)
   * @param sort String specifying sorting params
   * @return list of {@link GiftCertificate}
   */
  List<GiftCertificate> filter(
      String[] tagNames, String name, String description, String[] sort, int page, int pageSize);

  /**
   * Retrieve certificate by its unique id.
   *
   * @param id certificate id
   * @return {@link GiftCertificate} wrapped by {@link Optional}
   */
  Optional<GiftCertificate> findById(long id);

  /**
   * Create a new certificate in the storage.
   *
   * @param certificate {@link GiftCertificate} instance
   * @return {@link GiftCertificate} instance
   */
  GiftCertificate create(GiftCertificate certificate);

  /**
   * Update an existing certificate in the storage.
   *
   * @param certificate {@link GiftCertificate} instance
   * @return {@link GiftCertificate} instance
   */
  GiftCertificate update(GiftCertificate certificate);

  /**
   * Delete an existing certificate from the storage.
   *
   * @param certificate {@link GiftCertificate} instance
   */
  void delete(GiftCertificate certificate);
}
