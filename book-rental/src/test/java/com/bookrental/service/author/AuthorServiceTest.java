package com.bookrental.service.author;

import com.bookrental.api.author.request.CreateAuthorRequest;
import com.bookrental.api.author.request.GetAuthorParams;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.PersistenceUtil;
import com.bookrental.persistence.entity.Author;
import com.bookrental.persistence.repositories.AuthorRepository;
import com.bookrental.service.ServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private ServiceUtil serviceUtil;

    @Mock
    private PersistenceUtil persistenceUtil;

    @InjectMocks
    private AuthorService authorService;

    private static final String PUBLIC_ID = "author123";
    private static final String AUTHOR_NAME = "Jane Austen";
    private static final int BIRTH_YEAR = 1775;

    private CreateAuthorRequest createAuthorRequest;

    @BeforeEach
    void setUp() {
        createAuthorRequest = CreateAuthorRequest.builder()
                .name(AUTHOR_NAME)
                .birthYear(BIRTH_YEAR)
                .build();
    }

    @Test
    void createAuthor_CreatesNewAuthor() {
        // Given
        when(serviceUtil.generateRandomUUID()).thenReturn(PUBLIC_ID);
        when(authorRepository.save(any(Author.class))).thenReturn(createTestAuthor());

        // When
        AuthorDto result = authorService.createAuthor(createAuthorRequest);

        // Then
        assertNotNull(result);
        assertEquals(AUTHOR_NAME, result.getName());
        assertEquals(BIRTH_YEAR, result.getBirthYear());
        assertEquals(PUBLIC_ID, result.getPublicId());

        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void createAuthor_ThrowsExceptionWhenSaveFails() {
        // Given
        when(serviceUtil.generateRandomUUID()).thenReturn(PUBLIC_ID);
        doThrow(new RuntimeException("Database error")).when(authorRepository).save(any(Author.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authorService.createAuthor(createAuthorRequest);
        });

        assertEquals("Database error", exception.getMessage());
        verify(authorRepository, times(1)).save(any(Author.class));
        verify(authorRepository, times(1)).deleteByPublicId(PUBLIC_ID);
    }

    @Test
    void getAuthors_WhenAuthorsExist_ReturnsAuthorList() {
        // Given
        GetAuthorParams params = createGetAuthorParams();
        when(persistenceUtil.buildPageable(eq(params.getSize()), eq(params.getPage()), any(), any()))
                .thenReturn(PageRequest.of(params.getPage(), params.getSize()));
        when(persistenceUtil.buildAuthorListSpecification(params.getName()))
                .thenReturn(getSpecification());
        when(authorRepository.findAll(any(getSpecification().getClass()), any(Pageable.class)))
                .thenReturn(List.of(createTestAuthor()));

        // When
        List<AuthorDto> result = authorService.getAuthors(params);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AUTHOR_NAME, result.get(0).getName());
        assertEquals(PUBLIC_ID, result.get(0).getPublicId());
    }

    @Test
    void getAuthors_WhenNoAuthorsExist_ThrowsResourceNotFoundException() {
        // Given
        GetAuthorParams params = createGetAuthorParams();
        when(persistenceUtil.buildPageable(eq(params.getSize()), eq(params.getPage()), any(), any()))
                .thenReturn(PageRequest.of(params.getPage(), params.getSize()));
        when(persistenceUtil.buildAuthorListSpecification(params.getName()))
                .thenReturn(getSpecification());
        when(authorRepository.findAll(any(getSpecification().getClass()), any(Pageable.class)))
                .thenReturn(List.of());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            authorService.getAuthors(params);
        });
        assertEquals("No authors found.", exception.getMessage());
    }

    private Author createTestAuthor() {
        return Author.builder()
                .publicId(PUBLIC_ID)
                .name(AUTHOR_NAME)
                .birthYear(BIRTH_YEAR)
                .build();
    }

    private GetAuthorParams createGetAuthorParams() {
        return GetAuthorParams.builder()
                .name(AUTHOR_NAME)
                .page(0)
                .size(10)
                .build();
    }

    private Specification<Author> getSpecification() {
        return (root, query, criteriaBuilder) -> null;
    }

}
