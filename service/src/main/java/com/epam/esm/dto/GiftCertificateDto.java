package com.epam.esm.dto;

import com.epam.esm.dto.serialization.PeriodDeserializer;
import com.epam.esm.dto.serialization.PeriodSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.Set;

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
}
