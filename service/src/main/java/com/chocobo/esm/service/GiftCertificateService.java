package com.chocobo.esm.service;

import com.chocobo.esm.dto.GiftCertificateDto;
import com.chocobo.esm.dto.TagDto;
import com.chocobo.esm.entity.GiftCertificate;
import com.chocobo.esm.entity.Tag;
import com.chocobo.esm.exception.EntityNotFoundException;
import com.chocobo.esm.repository.GiftCertificateRepository;
import com.chocobo.esm.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class GiftCertificateService {

  private final GiftCertificateRepository giftCertificateRepository;
  private final TagRepository tagRepository;

  public GiftCertificateService(
      GiftCertificateRepository giftCertificateRepository, TagRepository tagRepository) {
    this.giftCertificateRepository = giftCertificateRepository;
    this.tagRepository = tagRepository;
  }

  public List<GiftCertificateDto> filter(
      String tagName,
      String certificateName,
      String description,
      String sort
  ) {
    List<GiftCertificate> giftCertificates =
            giftCertificateRepository.filter(tagName, certificateName, description, sort);
    return giftCertificates.stream()
        .map(
            giftCertificate -> {
              long id = giftCertificate.getId();
              List<Tag> tags = tagRepository.findByCertificateId(id);
              return GiftCertificateDto.convertToDto(giftCertificate, tags);
            })
        .toList();
  }

  public GiftCertificateDto findById(long id) {
    GiftCertificate certificate =
        giftCertificateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    List<Tag> tags = tagRepository.findByCertificateId(id);
    return GiftCertificateDto.convertToDto(certificate, tags);
  }

  @Transactional
  public GiftCertificateDto create(GiftCertificateDto certificateDto) {
    GiftCertificate certificate = certificateDto.convertToEntity();
    List<TagDto> tagDtos = certificateDto.getTagDtos();

    // TODO: 11/1/2021 implement validation

    Instant createDate = Instant.now();
    certificate.setCreateDate(createDate);
    certificate.setLastUpdateDate(createDate);

    long certificateId = giftCertificateRepository.create(certificate);

    if (tagDtos != null) {
      processTags(certificateId, tagDtos);
      certificateDto.setTagDtos(tagDtos);
    }

    return findById(certificateId);
  }

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

    List<TagDto> tagDtos = certificateDto.getTagDtos();

    // TODO: 11/1/2021 implement validation

    Instant lastUpdateDate = Instant.now();
    certificate.setLastUpdateDate(lastUpdateDate);
    giftCertificateRepository.update(certificate);

    if (tagDtos != null) {
      processTags(certificate.getId(), tagDtos);
    }

    return findById(certificate.getId());
  }

  public void delete(long id) {
    boolean deleted = giftCertificateRepository.delete(id);

    if (!deleted) {
      throw new EntityNotFoundException(id);
    }
  }

  private void processTags(long certificateId, List<TagDto> tagDtos) {
    List<Tag> currentTags = tagRepository.findByCertificateId(certificateId);
    currentTags.forEach(tag -> giftCertificateRepository.removeTag(certificateId, tag.getId()));

    tagDtos.forEach(
        tagDto -> {
          // TODO: 11/1/2021 implement validation

          String tagName = tagDto.getName();
          Optional<Tag> tag = tagRepository.findByName(tagName);

          long tagId;
          if (tag.isEmpty()) {
            Tag newTag = new Tag();
            newTag.setName(tagName);
            tagId = tagRepository.create(newTag);
          } else {
            tagId = tag.get().getId();
          }

          giftCertificateRepository.addTag(certificateId, tagId);
        });
  }
}
