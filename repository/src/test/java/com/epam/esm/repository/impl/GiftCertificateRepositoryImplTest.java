package com.epam.esm.repository.impl;

import com.epam.esm.config.DatabaseConfig;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.resolver.TestProfileResolver;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DatabaseConfig.class)
@Transactional
@ActiveProfiles(resolver = TestProfileResolver.class)
class GiftCertificateRepositoryImplTest {

  @Autowired private GiftCertificateRepository certificateRepository;

  @Autowired private TagRepository tagRepository;

  private static Stream<Arguments> provideCertificateSearchParams() {
    return Stream.of(
        Arguments.of(4, null, null, null),
        Arguments.of(1, "tag1", null, null),
        Arguments.of(0, "tagg", "name", "desc"),
        Arguments.of(2, null, null, "description"),
        Arguments.of(2, null, "cert", null));
  }

  @ParameterizedTest
  @MethodSource("provideCertificateSearchParams")
  void testFindByParams(
      long expectedSize, String tagName, String certificateName, String certificateDescription) {
    List<GiftCertificate> certificates =
        certificateRepository.filter(tagName, certificateName, certificateDescription, null);

    boolean valid =
        certificates.stream()
            .allMatch(
                cert -> {
                  boolean isValid =
                      certificateName == null
                          || StringUtils.containsIgnoreCase(cert.getName(), certificateName);
                  isValid &=
                      certificateDescription == null
                          || StringUtils.containsIgnoreCase(
                              cert.getDescription(), certificateDescription);
                  return isValid;
                });

    assertTrue(valid && certificates.size() == expectedSize);
  }

  @Test
  void testSortByNameAscending() {
    String sort = "name.asc";

    List<GiftCertificate> actual = certificateRepository.filter(null, null, null, sort);
    List<GiftCertificate> expected =
        actual.stream().sorted(Comparator.comparing(GiftCertificate::getName)).toList();

    assertEquals(expected, actual);
  }

  @Test
  void testSortByNameDescending() {
    String sort = "name.desc";

    List<GiftCertificate> actual = certificateRepository.filter(null, null, null, sort);
    List<GiftCertificate> expected =
        actual.stream()
            .sorted(Collections.reverseOrder(Comparator.comparing(GiftCertificate::getName)))
            .toList();

    assertEquals(expected, actual);
  }

  @Test
  void testSortByLastUpdateDateAscending() {
    String sort = "lastUpdateDate.asc";

    List<GiftCertificate> actual = certificateRepository.filter(null, null, null, sort);

    List<GiftCertificate> expected =
        actual.stream().sorted(Comparator.comparing(GiftCertificate::getCreateDate)).toList();

    assertEquals(expected, actual);
  }

  @Test
  void testSortByLastUpdateDateDescending() {
    String sort = "lastUpdateDate.desc";

    List<GiftCertificate> actual = certificateRepository.filter(null, null, null, sort);

    List<GiftCertificate> expected =
        actual.stream()
            .sorted(Collections.reverseOrder(Comparator.comparing(GiftCertificate::getCreateDate)))
            .toList();

    assertEquals(expected, actual);
  }

  @Test
  void testFindById() {
    Optional<GiftCertificate> certificate = certificateRepository.findById(1);
    assertTrue(certificate.isPresent() && certificate.get().getId() == 1);
  }

  @Test
  void testFindByIdNotFound() {
    Optional<GiftCertificate> certificate = certificateRepository.findById(0);
    assertTrue(certificate.isEmpty());
  }

  @Test
  void testAddRelation() {
    certificateRepository.addCertificateTagRelation(2, 2);
    Set<Tag> tags = tagRepository.findByCertificateId(2);

    assertEquals(1, tags.size());
  }

  @Test
  void testRemoveRelation() {
    certificateRepository.removeCertificateTagRelation(1, 1);
    Set<Tag> tags = tagRepository.findByCertificateId(1);

    assertTrue(tags.isEmpty());
  }

  @Test
  void testCreate() {
    GiftCertificate newCertificate = provideCertificate();

    long expectedId = 5;
    long actualId = certificateRepository.create(newCertificate);

    assertEquals(expectedId, actualId);
  }

  @Test
  void testUpdate() {
    GiftCertificate updatedCertificate = provideCertificate();
    updatedCertificate.setId(1);

    boolean result = certificateRepository.update(updatedCertificate);

    assertTrue(result);
  }

  @Test
  void testUpdateNotFound() {
    GiftCertificate updatedCertificate = provideCertificate();
    updatedCertificate.setId(0);

    boolean result = certificateRepository.update(updatedCertificate);

    assertFalse(result);
  }

  @Test
  void testDelete() {
    boolean result = certificateRepository.delete(1);

    assertTrue(result);
  }

  @Test
  void testDeleteNotFound() {
    boolean result = certificateRepository.delete(0);

    assertFalse(result);
  }

  private GiftCertificate provideCertificate() {
    GiftCertificate certificate = new GiftCertificate();
    certificate.setName("test");
    certificate.setDescription("test");
    certificate.setPrice(BigDecimal.ONE);
    certificate.setDuration(Period.ofDays(1));
    certificate.setCreateDate(Instant.now());
    certificate.setLastUpdateDate(Instant.now());

    return certificate;
  }
}
