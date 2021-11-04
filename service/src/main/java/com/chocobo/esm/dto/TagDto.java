package com.chocobo.esm.dto;

import com.chocobo.esm.entity.Tag;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class TagDto {

  private long id;

  private String name;

  public static TagDto convertToDto(Tag tag) {
    return new ModelMapper().map(tag, TagDto.class);
  }

  public Tag convertToEntity() {
    return new ModelMapper().map(this, Tag.class);
  }
}
