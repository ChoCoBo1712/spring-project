package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.dto.converter.GiftCertificateConverter;
import com.epam.esm.dto.converter.TagConverter;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.exception.InvalidSortParamsException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.GiftCertificateValidator;
import com.epam.esm.validator.SortArrayValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This service class encapsulated business logic related to {@link GiftCertificate} entity.
 *
 * @author Evgeniy Sokolchik
 */
@Service
@RequiredArgsConstructor
public class GiftCertificateService {

  @NonNull
  private final GiftCertificateRepository giftCertificateRepository;
  @NonNull
  private final TagRepository tagRepository;
  @NonNull
  private final GiftCertificateValidator giftCertificateValidator;
  @NonNull
  private final TagValidator tagValidator;
  @NonNull
  private final SortArrayValidator sortArrayValidator;

  /**
   * Retrieve certificates according to specified parameters. All parameters are optional, so if
   * they are not present, all certificates will be retrieved.
   *
   * @param tagNames String array specifying {@code Tag} entity name value
   * @param name String specifying {@code GiftCertificate} entity name value
   * @param description String specifying {@code GiftCertificate} entity description value
   * @param sort String specifying sorting params
   * @throws InvalidSortParamsException in case when sort param is not specified right
   * @return list of {@link GiftCertificateDto}
   */
  public List<GiftCertificateDto> filter(
          String[] tagNames, String name, String description, String[] sort) {
    boolean valid = sortArrayValidator.validate(sort);
    if (!valid) {
      throw new InvalidSortParamsException();
    }

    List<GiftCertificate> giftCertificates =
        giftCertificateRepository.filter(tagNames, name, description, sort);
    return giftCertificates.stream()
        .map(
            giftCertificate -> {
              Set<Tag> tags = giftCertificate.getTags();
              return GiftCertificateConverter.convertToDto(giftCertificate, tags);
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
    return GiftCertificateConverter.convertToDto(certificate, certificate.getTags());
  }

  /**
   * Create new certificate.
   *
   * @param certificateDto {@link GiftCertificateDto} instance
   * @throws InvalidEntityException in case when passed DTO object contains invalid data
   * @return {@link GiftCertificateDto} object that represents created {@link GiftCertificate}
   */
  @Transactional
  public GiftCertificateDto create(GiftCertificateDto certificateDto) {
    Set<TagDto> tagDtos = certificateDto.getTags();

    Instant createDate = Instant.now();
    certificateDto.setCreateDate(createDate);
    certificateDto.setLastUpdateDate(createDate);

    GiftCertificate certificate = GiftCertificateConverter.convertToEntity(certificateDto);
    List<ValidationError> validationErrors = giftCertificateValidator.validate(certificate);
    if (!validationErrors.isEmpty()) {
      throw new InvalidEntityException(validationErrors, GiftCertificate.class);
    }

    if (tagDtos != null) {
      processTags(certificate, tagDtos);
    }

    certificate = giftCertificateRepository.create(certificate);
    return GiftCertificateConverter.convertToDto(certificate, certificate.getTags());
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
    if (tagDtos != null) {
      processTags(certificate, tagDtos);
    }

    Instant lastUpdateDate = Instant.now();
    certificate.setLastUpdateDate(lastUpdateDate);
    giftCertificateRepository.update(certificate);

    return GiftCertificateConverter.convertToDto(certificate, certificate.getTags());
  }

  /**
   * Delete an existing certificate.
   *
   * @param id certificate id
   * @throws EntityNotFoundException in case when certificate with this id does not exist
   */
  @Transactional
  public void delete(long id) {
    GiftCertificate certificate = giftCertificateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    giftCertificateRepository.delete(certificate);
  }

  private void processTags(GiftCertificate certificate, Set<TagDto> tagDtos) {
    Set<Tag> tags = new HashSet<>();

    for (TagDto tagDto : tagDtos) {
      String tagName = tagDto.getName();
      List<ValidationError> validationErrors = tagValidator.validate(tagName);
      if (!validationErrors.isEmpty()) {
        throw new InvalidEntityException(validationErrors, Tag.class);
      }

      Optional<Tag> tag = tagRepository.findByName(tagName);
      if (tag.isEmpty()) {
        Tag newTag = TagConverter.convertToEntity(tagDto);
        newTag = tagRepository.create(newTag);
        tags.add(newTag);
      } else {
        tags.add(tag.get());
      }
    }

    certificate.setTags(tags);
  }
}
