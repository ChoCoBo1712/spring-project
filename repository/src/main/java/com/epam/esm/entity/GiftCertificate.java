package com.epam.esm.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;

@Data
public class GiftCertificate {

  private long id;

  private String name;
  private String description;
  private BigDecimal price;
  private Period duration;
  private Instant createDate;
  private Instant lastUpdateDate;
}
