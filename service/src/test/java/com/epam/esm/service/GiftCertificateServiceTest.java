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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.epam.esm.validator.ValidationError.INVALID_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GiftCertificateServiceTest {

  private static final Instant INITIAL_DATE = Instant.now();

  @InjectMocks private GiftCertificateService certificateService;

  @Mock private GiftCertificateRepository certificateRepository;

  @Mock private TagRepository tagRepository;

  @Mock private SortStringValidator sortStringValidator;

  @Mock private GiftCertificateValidator certificateValidator;

  @Mock private TagValidator tagValidator;

  @Captor private ArgumentCaptor<GiftCertificate> certificateCaptor;

  @BeforeAll
  static void setUp() {
    MockitoAnnotations.openMocks(GiftCertificateServiceTest.class);
  }

  @Test
  void testFilter() {
    List<GiftCertificate> certificateList =
        new ArrayList<>() {
          {
            add(provideCertificate());
          }
        };

    List<GiftCertificateDto> expectedDtoList =
        new ArrayList<>() {
          {
            add(provideCertificateDto());
          }
        };

    long certificateId = 1;
    String tagName = "tag";
    String name = "certificate";
    String description = "description";
    String sort = "name.asc,lastUpdateDate.desc";
    when(certificateRepository.filter(tagName, name, description, sort))
        .thenReturn(certificateList);
    when(tagRepository.findByCertificateId(certificateId)).thenReturn(provideTags());
    when(sortStringValidator.validate(sort)).thenReturn(true);

    List<GiftCertificateDto> actualDtoList =
        certificateService.filter(tagName, name, description, sort);

    verify(certificateRepository).filter(tagName, name, description, sort);
    assertEquals(expectedDtoList, actualDtoList);
  }

  @Test
  void testFilterInvalidSortString() {
    String tagName = "tag";
    String name = "certificate";
    String description = "description";
    String sort = "nameasc,lastUpdateDate.desc";
    when(sortStringValidator.validate(sort)).thenReturn(false);

    assertThrows(
        InvalidSortStringException.class,
        () -> certificateService.filter(tagName, name, description, sort));
  }

  @Test
  void testFindById() {
    long certificateId = 1;
    GiftCertificate certificate = provideCertificate();
    GiftCertificateDto expectedCertificateDto = provideCertificateDto();

    when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
    when(tagRepository.findByCertificateId(certificateId)).thenReturn(provideTags());

    GiftCertificateDto actualCertificateDto = certificateService.findById(certificateId);

    verify(certificateRepository).findById(anyLong());
    verify(tagRepository).findByCertificateId(anyLong());

    assertEquals(expectedCertificateDto, actualCertificateDto);
  }

  @Test
  void testFindByIdEntityNotFound() {
    int certificateId = 1;
    when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> certificateService.findById(certificateId));
  }

  @Test
  void testCreate() {
    long certificateId = 1;
    GiftCertificateDto certificateDto = provideCertificateDto();
    when(certificateRepository.create(any(GiftCertificate.class))).thenReturn(certificateId);

    certificateService.create(certificateDto);

    verify(certificateValidator).validate(certificateCaptor.capture());
  }

  @Test
  void testCreateInvalidEntity() {
    GiftCertificateDto certificateDto = provideCertificateDto();
    certificateDto.setName(null);

    GiftCertificate certificate = provideCertificate();
    certificate.setName(null);
    certificate.setCreateDate(null);
    certificate.setLastUpdateDate(null);

    List<ValidationError> errorList = List.of(INVALID_NAME);
    when(certificateValidator.validate(any(GiftCertificate.class))).thenReturn(errorList);

    assertThrows(InvalidEntityException.class, () -> certificateService.create(certificateDto));
  }

  @Test
  void testUpdate() {
    long certificateId = 1;
    long tagId = 1;
    String tagName = "tag";
    GiftCertificate certificate = provideCertificate();
    GiftCertificateDto updatedCertificateDto = provideCertificateDto();
    updatedCertificateDto.setId(certificateId);

    Tag newTag = new Tag();
    newTag.setId(tagId);
    newTag.setName(tagName);

    when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
    when(certificateRepository.update(certificate)).thenReturn(true);
    when(tagRepository.findByCertificateId(certificateId)).thenReturn(provideTags());
    when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());
    when(tagRepository.create(newTag)).thenReturn(tagId);

    certificateService.update(updatedCertificateDto);

    verify(certificateValidator).validate(certificateCaptor.capture());
    GiftCertificate capturedCertificate = certificateCaptor.getValue();
    verify(certificateRepository).update(capturedCertificate);
    verify(tagValidator).validate(tagName);
    verify(tagRepository).findByName(tagName);
    verify(certificateRepository).addCertificateTagRelation(certificateId, tagId);
  }

  @Test
  void testUpdateEntityNotFound() {
    long certificateId = 1;
    GiftCertificateDto updatedCertificateDto = provideCertificateDto();

    when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());
    assertThrows(
        EntityNotFoundException.class, () -> certificateService.update(updatedCertificateDto));
  }

  @Test
  void testUpdateInvalidCertificateEntity() {
    long certificateId = 1;
    GiftCertificate certificate = provideCertificate();
    GiftCertificateDto updatedCertificateDto = provideCertificateDto();
    updatedCertificateDto.setName("");

    List<ValidationError> errorList = List.of(INVALID_NAME);
    when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
    when(certificateValidator.validate(certificate)).thenReturn(errorList);

    assertThrows(
        InvalidEntityException.class, () -> certificateService.update(updatedCertificateDto));
  }

  @Test
  void testUpdateInvalidTagEntity() {
    String tagName = "tag";
    long certificateId = 1;
    GiftCertificate certificate = provideCertificate();
    GiftCertificateDto updatedCertificateDto = provideCertificateDto();
    updatedCertificateDto.setTags(
        new HashSet<>() {
          {
            add(provideTagDto());
          }
        });

    List<ValidationError> errorList = List.of(INVALID_NAME);
    when(tagValidator.validate(tagName)).thenReturn(errorList);
    when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));

    assertThrows(
        InvalidEntityException.class, () -> certificateService.update(updatedCertificateDto));
  }

  @Test
  void testDelete() {
    long certificateId = 1;
    when(certificateRepository.delete(certificateId)).thenReturn(true);

    certificateService.delete(certificateId);

    verify(certificateRepository).delete(certificateId);
  }

  @Test
  void testDeleteEntityNotFound() {
    long certificateId = 1;
    when(certificateRepository.delete(certificateId)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> certificateService.delete(certificateId));

    verify(certificateRepository).delete(certificateId);
  }

  private GiftCertificate provideCertificate() {
    GiftCertificate certificate = new GiftCertificate();

    certificate.setId(1);
    certificate.setName("certificate");
    certificate.setDescription("description");
    certificate.setPrice(BigDecimal.ONE);
    certificate.setDuration(Period.ofDays(1));
    certificate.setCreateDate(INITIAL_DATE);
    certificate.setLastUpdateDate(INITIAL_DATE);

    return certificate;
  }

  private GiftCertificateDto provideCertificateDto() {
    GiftCertificateDto certificateDto = new GiftCertificateDto();

    certificateDto.setId(1);
    certificateDto.setName("certificate");
    certificateDto.setDescription("description");
    certificateDto.setPrice(BigDecimal.ONE);
    certificateDto.setDuration(Period.ofDays(1));
    certificateDto.setCreateDate(INITIAL_DATE);
    certificateDto.setLastUpdateDate(INITIAL_DATE);
    certificateDto.setTags(provideTagDtos());

    return certificateDto;
  }

  private Set<Tag> provideTags() {
    Tag tag = new Tag();

    tag.setId(1);
    tag.setName("tag");

    return new HashSet<>() {
      {
        add(tag);
      }
    };
  }

  private Set<TagDto> provideTagDtos() {
    TagDto tagDto = new TagDto();

    tagDto.setId(1);
    tagDto.setName("tag");

    return new HashSet<>() {
      {
        add(tagDto);
      }
    };
  }

  private TagDto provideTagDto() {
    TagDto tagDto = new TagDto();
    tagDto.setId(1);
    tagDto.setName("tag");
    return tagDto;
  }
}
