package com.epam.esm.dto;

import com.epam.esm.dto.serialization.PeriodDeserializer;
import com.epam.esm.dto.serialization.PeriodSerializer;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class GiftCertificateDto {

  private long id;

  private String name;

  private String description;

  private BigDecimal price;

  @JsonSerialize(using = PeriodSerializer.class)
  @JsonDeserialize(using = PeriodDeserializer.class)
  private Period duration;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
      timezone = "UTC")
  private Instant createDate;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
      timezone = "UTC")
  private Instant lastUpdateDate;

  private Set<TagDto> tags;

  public static GiftCertificateDto convertToDto(GiftCertificate giftCertificate, Set<Tag> tags) {
    GiftCertificateDto giftCertificateDto =
        new ModelMapper().map(giftCertificate, GiftCertificateDto.class);
    giftCertificateDto.tags = tags.stream().map(TagDto::convertToDto).collect(Collectors.toSet());

    return giftCertificateDto;
  }

  public GiftCertificate convertToEntity() {
    return new ModelMapper().map(this, GiftCertificate.class);
  }
}
