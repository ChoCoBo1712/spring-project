package com.chocobo.esm.service;

import com.chocobo.esm.dto.TagDto;
import com.chocobo.esm.entity.Tag;
import com.chocobo.esm.exception.EntityAlreadyExistsException;
import com.chocobo.esm.exception.EntityNotFoundException;
import com.chocobo.esm.exception.InvalidEntityException;
import com.chocobo.esm.repository.TagRepository;
import com.chocobo.esm.validator.TagValidator;
import com.chocobo.esm.validator.ValidationError;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service class encapsulated business logic related to {@link Tag} entity.
 *
 * @author Evgeniy Sokolchik
 */
@Service
public class TagService {

  private final TagRepository tagRepository;
  private final TagValidator tagValidator;

  public TagService(TagRepository tagRepository, TagValidator tagValidator) {
    this.tagRepository = tagRepository;
    this.tagValidator = tagValidator;
  }

  /**
   * Retrieve all tags.
   *
   * @return list of {@link TagDto}
   */
  public List<TagDto> findAll() {
    return tagRepository.findAll().stream().map(TagDto::convertToDto).toList();
  }

  /**
   * Retrieve tag by its unique id.
   *
   * @param id tag id
   * @throws EntityNotFoundException in case when tag with this id does not exist
   * @return {@link TagDto} object
   */
  public TagDto findById(long id) {
    Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    return TagDto.convertToDto(tag);
  }

  /**
   * Create a new tag.
   *
   * @param tagDto {@link TagDto} instance
   * @throws InvalidEntityException in case when passed DTO object contains invalid data
   * @throws EntityAlreadyExistsException in case when tag with specified name already exists
   * @return {@link TagDto} object that represents created tag
   */
  public TagDto create(TagDto tagDto) {
    Tag tag = tagDto.convertToEntity();

    List<ValidationError> validationErrors = tagValidator.validate(tag.getName());
    if (!validationErrors.isEmpty()) {
      throw new InvalidEntityException(validationErrors, Tag.class);
    }

    String name = tag.getName();
    if (tagRepository.findByName(name).isPresent()) {
      throw new EntityAlreadyExistsException();
    }

    long tagId = tagRepository.create(tag);
    tagDto.setId(tagId);
    return tagDto;
  }

  /**
   * Delete an existing tag.
   *
   * @param id tag id
   * @throws EntityNotFoundException in case when tag with this id does not exist
   */
  public void delete(long id) {
    boolean deleted = tagRepository.delete(id);

    if (!deleted) {
      throw new EntityNotFoundException(id);
    }
  }
}
