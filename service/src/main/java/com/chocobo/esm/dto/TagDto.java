package com.chocobo.esm.dto;

import com.chocobo.esm.entity.Tag;
import lombok.Data;

@Data
public class TagDto {

    private long id;

    private String name;

    public static TagDto convertToDto(Tag tag) {
        TagDto tagDto = new TagDto();
        tagDto.id = tag.getId();
        tagDto.name = tagDto.getName();
        return tagDto;
    }

    public Tag convertToEntity() {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }
}
