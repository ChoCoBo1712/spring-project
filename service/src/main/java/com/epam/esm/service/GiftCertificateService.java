package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.exception.InvalidSortStringException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.GiftCertificateValidator;
import com.epam.esm.validator.SortStringValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This service class encapsulated business logic related to {@link GiftCertificate} entity.
 *
 * @author Evgeniy Sokolchik
 */
@Service
public class GiftCertificateService {

  private final GiftCertificateRepository giftCertificateRepository;
  private final TagRepository tagRepository;
  private final GiftCertificateValidator giftCertificateValidator;
  private final TagValidator tagValidator;
  private final SortStringValidator sortStringValidator;

  public GiftCertificateService(
      GiftCertificateRepository giftCertificateRepository,
      TagRepository tagRepository,
      GiftCertificateValidator giftCertificateValidator,
      TagValidator tagValidator,
      SortStringValidator sortStringValidator) {
    this.giftCertificateRepository = giftCertificateRepository;
    this.tagRepository = tagRepository;
    this.giftCertificateValidator = giftCertificateValidator;
    this.tagValidator = tagValidator;
    this.sortStringValidator = sortStringValidator;
  }

  /**
   * Retrieve certificates according to specified parameters. All parameters are optional, so if
   * they are not present, all certificates will be retrieved.
   *
   * @param tagName String specifying {@code Tag} entity name value
   * @param name String specifying {@code GiftCertificate} entity name value
   * @param description String specifying {@code GiftCertificate} entity description value
   * @param sort String specifying sorting params
   * @throws InvalidSortStringException in case when sort param is not specified right
   * @return list of {@link GiftCertificateDto}
   */
  public List<GiftCertificateDto> filter(
      String tagName, String name, String description, String sort) {
    boolean valid = sortStringValidator.validate(sort);
    if (!valid) {
      throw new InvalidSortStringException();
    }

    List<GiftCertificate> giftCertificates =
        giftCertificateRepository.filter(tagName, name, description, sort);
    return giftCertificates.stream()
        .map(
            giftCertificate -> {
              long id = giftCertificate.getId();
              Set<Tag> tags = tagRepository.findByCertificateId(id);
              return GiftCertificateDto.convertToDto(giftCertificate, tags);
            })
        .toList();
  }

  /**
   * Retrieve certificate by its unique id.
   *
   * @param id certificate id
   * @throws EntityNotFoundException in case when certificate with this id does not exist
   * @return {@link GiftCertificateDto} object
   */
  public GiftCertificateDto findById(long id) {
    GiftCertificate certificate =
        giftCertificateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    Set<Tag> tags = tagRepository.findByCertificateId(id);
    return GiftCertificateDto.convertToDto(certificate, tags);
  }

  /**
   * Create new certificate.
   *
   * @param certificateDto {@link GiftCertificateDto} instance
   * @throws InvalidEntityException in case when passed DTO object contains invalid data
   * @return {@link GiftCertificateDto} object that represents created {@link GiftCertificate}
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public GiftCertificateDto create(GiftCertificateDto certificateDto) {
    Set<TagDto> tagDtos = certificateDto.getTags();

    Instant createDate = Instant.now();
    certificateDto.setCreateDate(createDate);
    certificateDto.setLastUpdateDate(createDate);

    GiftCertificate certificate = certificateDto.convertToEntity();
    List<ValidationError> validationErrors = giftCertificateValidator.validate(certificate);
    if (!validationErrors.isEmpty()) {
      throw new InvalidEntityException(validationErrors, GiftCertificate.class);
    }

    long certificateId = giftCertificateRepository.create(certificate);
    certificateDto.setId(certificateId);

    if (tagDtos != null) {
      processTags(certificateId, tagDtos);
      certificateDto.setTags(tagDtos);
    }

    return certificateDto;
  }

  /**
   * Update an existing certificate.
   *
   * @param certificateDto {@link GiftCertificateDto} instance
   * @throws EntityNotFoundException in case when certificate with this id does not exist
   * @throws InvalidEntityException in case when passed DTO object contains invalid data
   * @return updated {@link GiftCertificateDto} object
   */
  @Transactional
  public GiftCertificateDto update(GiftCertificateDto certificateDto) {
    long certificateId = certificateDto.getId();
    Optional<GiftCertificate> optionalCertificate =
        giftCertificateRepository.findById(certificateId);

    if (optionalCertificate.isEmpty()) {
      throw new EntityNotFoundException(certificateId);
    }

    GiftCertificate certificate = optionalCertificate.get();

    if (certificateDto.getName() != null) {
      certificate.setName(certificateDto.getName());
    }
    if (certificateDto.getDescription() != null) {
      certificate.setDescription(certificateDto.getDescription());
    }
    if (certificateDto.getDuration() != null) {
      certificate.setDuration(certificateDto.getDuration());
    }
    if (certificateDto.getPrice() != null) {
      certificate.setPrice(certificateDto.getPrice());
    }

    List<ValidationError> validationErrors = giftCertificateValidator.validate(certificate);
    if (!validationErrors.isEmpty()) {
      throw new InvalidEntityException(validationErrors, GiftCertificate.class);
    }

    Set<TagDto> tagDtos = certificateDto.getTags();

    Instant lastUpdateDate = Instant.now();
    certificate.setLastUpdateDate(lastUpdateDate);
    giftCertificateRepository.update(certificate);

    Set<Tag> tags;
    if (tagDtos != null) {
      processTags(certificate.getId(), tagDtos);
      tags = tagDtos.stream().map(TagDto::convertToEntity).collect(Collectors.toSet());
    } else {
      tags = Collections.emptySet();
    }

    return GiftCertificateDto.convertToDto(certificate, tags);
  }

  /**
   * Delete an existing certificate.
   *
   * @param id certificate id
   * @throws EntityNotFoundException in case when certificate with this id does not exist
   */
  public void delete(long id) {
    boolean deleted = giftCertificateRepository.delete(id);

    if (!deleted) {
      throw new EntityNotFoundException(id);
    }
  }

  private void processTags(long certificateId, Set<TagDto> tagDtos) {
    Set<Tag> currentTags = tagRepository.findByCertificateId(certificateId);
    currentTags.forEach(
        tag -> giftCertificateRepository.removeRelation(certificateId, tag.getId()));

    tagDtos.forEach(
        tagDto -> {
          String tagName = tagDto.getName();

          List<ValidationError> validationErrors = tagValidator.validate(tagName);
          if (!validationErrors.isEmpty()) {
            throw new InvalidEntityException(validationErrors, Tag.class);
          }

          Optional<Tag> tag = tagRepository.findByName(tagName);
          long tagId;
          if (tag.isEmpty()) {
            Tag newTag = tagDto.convertToEntity();
            tagId = tagRepository.create(newTag);
          } else {
            tagId = tag.get().getId();
          }

          tagDto.setId(tagId);

          giftCertificateRepository.addRelation(certificateId, tagId);
        });
  }
}
