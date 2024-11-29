package com.bookrental.api.book;

import com.bookrental.api.author.response.Author;
import com.bookrental.api.book.response.Book;
import com.bookrental.api.user.response.User;
import com.bookrental.service.author.AuthorDto;
import com.bookrental.service.book.BookDto;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookMapperTest {

    private final BookMapper bookMapper = new BookMapper();

    @Test
    void mapBookDtoToBook_ShouldMapCorrectly() throws IllegalAccessException {
        // Given
        List<AuthorDto> authorDtos = List.of(
                AuthorDto.builder()
                        .publicId("author-public-id-1")
                        .name("Author One")
                        .birthYear(1970)
                        .build(),
                AuthorDto.builder()
                        .publicId("author-public-id-2")
                        .name("Author Two")
                        .birthYear(1980)
                        .build()
        );

        BookDto bookDto = BookDto.builder()
                .publicId("book-public-id")
                .title("Sample Book")
                .isbn("1234567890123")
                .publisher("Sample Publisher")
                .amount(5)
                .publicationYear(2020)
                .authors(authorDtos)
                .build();

        // When
        Book book = bookMapper.mapBookDtoToBook(bookDto);

        // Then
        assertNotNull(book);
        assertEquals(bookDto.getPublicId(), book.getPublicId());
        assertEquals(bookDto.getTitle(), book.getTitle());
        assertEquals(bookDto.getIsbn(), book.getIsbn());
        assertEquals(bookDto.getPublisher(), book.getPublisher());
        assertEquals(bookDto.getAmount(), book.getAmount());
        assertEquals(bookDto.getPublicationYear(), book.getPublicationYear());
        assertNotNull(book.getAuthors());
        assertEquals(bookDto.getAuthors().size(), book.getAuthors().size());

        for (int i = 0; i < bookDto.getAuthors().size(); i++) {
            AuthorDto authorDto = bookDto.getAuthors().get(i);
            Author author = book.getAuthors().get(i);

            assertEquals(authorDto.getPublicId(), author.getPublicId());
            assertEquals(authorDto.getName(), author.getName());
            assertEquals(authorDto.getBirthYear(), author.getBirthYear());
        }

        for (Field field : Book.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(book);
            assertNotNull(value, "Field '" + field.getName() + "' should not be null");
        }
    }

    @Test
    void mapBookDtoToBook_ShouldHandleNullFieldsGracefully() {
        // Given
        BookDto bookDto = BookDto.builder()
                .publicId(null)
                .title(null)
                .isbn(null)
                .publisher(null)
                .amount(0)
                .publicationYear(0)
                .authors(null)
                .build();

        // When
        Book book = bookMapper.mapBookDtoToBook(bookDto);

        // Then
        assertNotNull(book);
        assertNull(book.getPublicId());
        assertNull(book.getTitle());
        assertNull(book.getIsbn());
        assertNull(book.getPublisher());
        assertEquals(0, book.getAmount());
        assertEquals(0, book.getPublicationYear());
        assertNull(book.getAuthors());
    }
}
