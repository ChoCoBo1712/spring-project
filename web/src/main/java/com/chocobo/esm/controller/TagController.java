package com.chocobo.esm.controller;

import com.chocobo.esm.dto.TagDto;
import com.chocobo.esm.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> getTags() {
        List<TagDto> tagDtos = tagService.findAll();
        return new ResponseEntity<>(tagDtos, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTag(@PathVariable("id") long id) {
        TagDto tagDto = tagService.findById(id);
        return new ResponseEntity<>(tagDto, OK);
    }

    @PostMapping
    public ResponseEntity<TagDto> createTag(@RequestBody TagDto tagDto) {
        TagDto createdTagDto = tagService.create(tagDto);
        return new ResponseEntity<>(createdTagDto, CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable("id") long id) {
        tagService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
