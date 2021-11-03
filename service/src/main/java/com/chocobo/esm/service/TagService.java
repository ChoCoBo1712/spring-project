package com.chocobo.esm.service;

import com.chocobo.esm.dto.TagDto;
import com.chocobo.esm.entity.Tag;
import com.chocobo.esm.exception.EntityNotFoundException;
import com.chocobo.esm.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDto> findAll() {
        return tagRepository.findAll().stream()
                .map(TagDto::convertToDto)
                .toList();
    }

    public TagDto findById(long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        return TagDto.convertToDto(tag);
    }

    public TagDto create(TagDto tagDto) {
        Tag tag = tagDto.convertToEntity();
        // TODO: 11/1/2021 implement validation
        long tagId = tagRepository.create(tag);
        tagDto.setId(tagId);

        return tagDto;
    }

    public void delete(long id) {
        boolean deleted = tagRepository.delete(id);

        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }
}
