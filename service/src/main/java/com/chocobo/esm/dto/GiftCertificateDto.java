package com.chocobo.esm.dto;

import com.chocobo.esm.entity.GiftCertificate;
import com.chocobo.esm.entity.Tag;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.List;

@Data
public class GiftCertificateDto {

    private long id;

    private String name;
    private String description;
    private BigDecimal price;
    private Period duration;
    private Instant createDate;
    private Instant lastUpdateDate;

    private List<TagDto> tagDtos;

    public static GiftCertificateDto convertToDto(GiftCertificate giftCertificate, List<Tag> tags) {
        GiftCertificateDto giftCertificateDto = new GiftCertificateDto();

        giftCertificateDto.id = giftCertificate.getId();
        giftCertificateDto.name = giftCertificate.getName();
        giftCertificateDto.description = giftCertificate.getDescription();
        giftCertificateDto.price = giftCertificate.getPrice();
        giftCertificateDto.duration = giftCertificate.getDuration();
        giftCertificateDto.createDate = giftCertificate.getCreateDate();
        giftCertificateDto.lastUpdateDate = giftCertificate.getLastUpdateDate();
        giftCertificateDto.tagDtos = tags.stream()
                .map(TagDto::convertToDto)
                .toList();

        return giftCertificateDto;
    }

    public GiftCertificate convertToEntity() {
        GiftCertificate giftCertificate = new GiftCertificate();

        giftCertificate.setId(id);
        giftCertificate.setName(name);
        giftCertificate.setDescription(description);
        giftCertificate.setPrice(price);
        giftCertificate.setDuration(duration);
        giftCertificate.setCreateDate(createDate);
        giftCertificate.setLastUpdateDate(lastUpdateDate);

        return giftCertificate;
    }
}
