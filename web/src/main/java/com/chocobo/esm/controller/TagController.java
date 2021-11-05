package com.chocobo.esm.controller;

import com.chocobo.esm.dto.TagDto;
import com.chocobo.esm.exception.EntityAlreadyExistsException;
import com.chocobo.esm.exception.EntityNotFoundException;
import com.chocobo.esm.exception.InvalidEntityException;
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

/**
 * This class contains public REST API endpoints related to {@code Tag} entity.
 *
 * @author Evgeniy Sokolchik
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

  private final TagService tagService;

  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  /**
   * Retrieve all tags.
   *
   * @return JSON {@link ResponseEntity} object that contains list of {@link TagDto}
   */
  @GetMapping
  public ResponseEntity<List<TagDto>> getTags() {
    List<TagDto> tagDtos = tagService.findAll();
    return new ResponseEntity<>(tagDtos, OK);
  }

  /**
   * Retrieve tag by its unique id.
   *
   * @param id tag id
   * @throws EntityNotFoundException in case when tag with this id does not exist
   * @return JSON {@link ResponseEntity} object that contains {@link TagDto} object
   */
  @GetMapping("/{id}")
  public ResponseEntity<TagDto> getTag(@PathVariable("id") long id) {
    TagDto tagDto = tagService.findById(id);
    return new ResponseEntity<>(tagDto, OK);
  }

  /**
   * Create a new tag.
   *
   * @param tagDto {@link TagDto} instance
   * @throws InvalidEntityException in case when passed DTO object contains invalid data
   * @throws EntityAlreadyExistsException in case when tag with specified name already exists
   * @return JSON {@link ResponseEntity} object that contains created {@link TagDto} object
   */
  @PostMapping
  public ResponseEntity<TagDto> createTag(@RequestBody TagDto tagDto) {
    TagDto createdDto = tagService.create(tagDto);
    return new ResponseEntity<>(createdDto, CREATED);
  }

  /**
   * Delete an existing tag.
   *
   * @param id tag id
   * @throws EntityNotFoundException in case when tag with this id does not exist
   * @return empty {@link ResponseEntity}
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(@PathVariable("id") long id) {
    tagService.delete(id);
    return new ResponseEntity<>(NO_CONTENT);
  }
}
