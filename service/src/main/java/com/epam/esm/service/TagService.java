package com.epam.esm.service;

import com.epam.esm.dto.TagDto;
import com.epam.esm.dto.converter.TagConverter;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * This service class encapsulated business logic related to {@link Tag} entity.
 *
 * @author Evgeniy Sokolchik
 */
@Service
@RequiredArgsConstructor
public class TagService {

  @NonNull
  private final TagRepository tagRepository;
  @NonNull
  private final TagValidator tagValidator;

  /**
   * Retrieve all tags.
   *
   * @return list of {@link TagDto}
   */
  public List<TagDto> findAll() {
    return tagRepository.findAll().stream().map(TagConverter::convertToDto).toList();
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
    return TagConverter.convertToDto(tag);
  }

  /**
   * Create a new tag.
   *
   * @param tagDto {@link TagDto} instance
   * @throws InvalidEntityException in case when passed DTO object contains invalid data
   * @throws EntityAlreadyExistsException in case when tag with specified name already exists
   * @return {@link TagDto} object that represents created tag
   */
  @Transactional
  public TagDto create(TagDto tagDto) {
    Tag tag = TagConverter.convertToEntity(tagDto);

    List<ValidationError> validationErrors = tagValidator.validate(tag.getName());
    if (!validationErrors.isEmpty()) {
      throw new InvalidEntityException(validationErrors, Tag.class);
    }

    String name = tag.getName();
    if (tagRepository.findByName(name).isPresent()) {
      throw new EntityAlreadyExistsException();
    }

    tag = tagRepository.create(tag);
    return TagConverter.convertToDto(tag);
  }

  /**
   * Delete an existing tag.
   *
   * @param id tag id
   * @throws EntityNotFoundException in case when tag with this id does not exist
   */
  @Transactional
  public void delete(long id) {
    Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    tagRepository.delete(tag);
  }
}
