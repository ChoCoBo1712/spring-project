package com.chocobo.esm.service;

import com.chocobo.esm.dto.TagDto;
import com.chocobo.esm.entity.Tag;
import com.chocobo.esm.exception.EntityAlreadyExistsException;
import com.chocobo.esm.exception.EntityNotFoundException;
import com.chocobo.esm.exception.InvalidEntityException;
import com.chocobo.esm.repository.TagRepository;
import com.chocobo.esm.validator.TagValidator;
import com.chocobo.esm.validator.ValidationError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.chocobo.esm.validator.ValidationError.INVALID_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagValidator tagValidator;

    @BeforeAll
    static void setUp() {
        MockitoAnnotations.openMocks(TagServiceTest.class);
    }

    @Test
    void testFindAll() {
        when(tagRepository.findAll()).thenReturn(provideTagsList());

        List<TagDto> expectedDtoList = provideTagDtoList();
        List<TagDto> actualDtoList = tagService.findAll();

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testFindById() {
        long tagId = 1;
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(provideTag()));

        TagDto expectedDto = provideTagDto();
        TagDto actualDto = tagService.findById(tagId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testFindByIdEntityNotFound() {
        long tagId = 1;
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.findById(tagId));
    }

    @Test
    void testCreate() {
        TagDto tagDto = provideTagDto();
        Tag tag = provideTag();

        when(tagRepository.findByName(tagDto.getName())).thenReturn(Optional.empty());

        tagService.create(tagDto);

        verify(tagValidator).validate(tag.getName());
        verify(tagRepository).findByName(tag.getName());
        verify(tagRepository).create(tag);
    }

    @Test
    void testCreateInvalidEntity() {
        String tagName = "";
        TagDto tagDto = provideTagDto();
        tagDto.setName(tagName);

        List<ValidationError> errorList = List.of(INVALID_NAME);
        when(tagValidator.validate(tagName)).thenReturn(errorList);

        assertThrows(InvalidEntityException.class, () -> tagService.create(tagDto));
    }

    @Test
    void testCreateEntityAlreadyExists() {
        TagDto tagDto = provideTagDto();
        Tag tag = provideTag();

        when(tagRepository.findByName(tagDto.getName())).thenReturn(Optional.of(tag));
        assertThrows(EntityAlreadyExistsException.class, () -> tagService.create(tagDto));

        verify(tagValidator).validate(tag.getName());
    }

    @Test
    void testDelete() {
        int tagId = 1;
        when(tagRepository.delete(tagId)).thenReturn(true);

        tagService.delete(tagId);

        verify(tagRepository).delete(tagId);
    }

    @Test
    void testDeleteEntityNotFound() {
        int tagId = 1;
        when(tagRepository.delete(tagId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> tagService.delete(tagId));
        verify(tagRepository).delete(tagId);
    }

    private Tag provideTag() {
        Tag tag = new Tag();
        tag.setId(1);
        tag.setName("tag");

        return tag;
    }

    private TagDto provideTagDto() {
        TagDto tagDto = new TagDto();
        tagDto.setId(1);
        tagDto.setName("tag");

        return tagDto;
    }

    private List<Tag> provideTagsList() {
        Tag firstTag = new Tag();
        firstTag.setId(1);
        firstTag.setName("tag1");

        Tag secondTag = new Tag();
        secondTag.setName("tag2");

        Tag thirdTag = new Tag();
        thirdTag.setName("tag3");

        return new ArrayList<>() {{
            add(firstTag);
            add(secondTag);
            add(thirdTag);
        }};
    }

    private List<TagDto> provideTagDtoList() {
        TagDto firstTagDto = new TagDto();
        firstTagDto.setId(1);
        firstTagDto.setName("tag1");

        TagDto secondTagDto = new TagDto();
        secondTagDto.setName("tag2");

        TagDto thirdTagDto = new TagDto();
        thirdTagDto.setName("tag3");

        return new ArrayList<>() {{
            add(firstTagDto);
            add(secondTagDto);
            add(thirdTagDto);
        }};
    }
}
