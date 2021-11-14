package com.epam.esm.entity;

import com.epam.esm.converter.PeriodConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "gift_certificates")
public class GiftCertificate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  private String description;

  private BigDecimal price;

  @Convert(converter = PeriodConverter.class)
  private Period duration;

  private Instant createDate;

  private Instant lastUpdateDate;

  @ManyToMany
  @JoinTable(
      name = "certificates_tags",
      joinColumns = @JoinColumn(name = "certificate_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private Set<Tag> tags;
}
