package com.bookrental.api.author;

import com.bookrental.api.author.response.Author;
import com.bookrental.service.author.AuthorDto;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class AuthorMapperTest {

    private final AuthorMapper authorMapper = new AuthorMapper();

    @Test
    void mapAuthorDtoToAuthor_ShouldMapCorrectly() throws IllegalAccessException {
        // Given
        AuthorDto authorDto = AuthorDto.builder()
                .publicId("12345")
                .name("Jane Austen")
                .birthYear(1775)
                .build();

        // When
        Author author = authorMapper.mapAuthorDtoToAuthor(authorDto);

        // Then
        assertNotNull(author);
        assertEquals(authorDto.getPublicId(), author.getPublicId());
        assertEquals(authorDto.getName(), author.getName());
        assertEquals(authorDto.getBirthYear(), author.getBirthYear());

        for (Field field : Author.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(author);
            assertNotNull(value, "Field '" + field.getName() + "' should not be null");
        }
    }

    @Test
    void mapAuthorDtoToAuthor_ShouldHandleNullFieldsGracefully() {
        // Given
        AuthorDto authorDto = AuthorDto.builder()
                .publicId(null)
                .name(null)
                .birthYear(0)
                .build();

        // When
        Author author = authorMapper.mapAuthorDtoToAuthor(authorDto);

        // Then
        assertNotNull(author);
        assertNull(author.getPublicId());
        assertNull(author.getName());
        assertEquals(0, authorDto.getBirthYear());
    }

}
