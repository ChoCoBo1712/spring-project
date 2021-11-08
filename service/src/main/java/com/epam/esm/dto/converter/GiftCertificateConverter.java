package com.epam.esm.dto.converter;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.modelmapper.ModelMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class GiftCertificateConverter {

  public static GiftCertificateDto convertToDto(GiftCertificate giftCertificate, Set<Tag> tags) {
    GiftCertificateDto giftCertificateDto =
        new ModelMapper().map(giftCertificate, GiftCertificateDto.class);
    Set<TagDto> tagDtos = tags.stream().map(TagConverter::convertToDto).collect(Collectors.toSet());
    giftCertificateDto.setTags(tagDtos);
    return giftCertificateDto;
  }

  public static GiftCertificate convertToEntity(GiftCertificateDto certificateDto) {
    return new ModelMapper().map(certificateDto, GiftCertificate.class);
  }
}
