package com.epam.esm.dto.converter;

import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import org.modelmapper.ModelMapper;

public class TagConverter {

  public static TagDto convertToDto(Tag tag) {
    return new ModelMapper().map(tag, TagDto.class);
  }

  public static Tag convertToEntity(TagDto tagDto) {
    return new ModelMapper().map(tagDto, Tag.class);
  }
}
