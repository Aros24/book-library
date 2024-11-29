package com.bookrental.service.book;

import com.bookrental.api.book.request.CreateBookRequest;
import com.bookrental.api.book.request.GetBookParams;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.PersistenceUtil;
import com.bookrental.persistence.entity.Author;
import com.bookrental.persistence.entity.Book;
import com.bookrental.persistence.repositories.AuthorRepository;
import com.bookrental.persistence.repositories.BookRepository;
import com.bookrental.service.ServiceUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private final BookRepository bookRepository = mock(BookRepository.class);
    private final AuthorRepository authorRepository = mock(AuthorRepository.class);
    private final ServiceUtil serviceUtil = mock(ServiceUtil.class);
    private final PersistenceUtil persistenceUtil = mock(PersistenceUtil.class);
    private final BookService bookService = new BookService(bookRepository, authorRepository, serviceUtil, persistenceUtil);

    private static final String SAMPLE_PUBLIC_ID = UUID.randomUUID().toString();
    private static final String SAMPLE_ISBN = "1234567890123";

    @Test
    void addBook_shouldAddBookSuccessfully() {
        // Given
        List<String> authorPublicIds = List.of(UUID.randomUUID().toString());
        List<Author> authors = List.of(createAuthor(authorPublicIds.get(0)));

        when(authorRepository.findByPublicIdIn(authorPublicIds)).thenReturn(authors);
        when(bookRepository.findBookByIsbn(SAMPLE_ISBN)).thenReturn(null);
        when(serviceUtil.generateRandomUUID()).thenReturn(SAMPLE_PUBLIC_ID);
        when(bookRepository.save(any())).thenReturn(createBook());

        CreateBookRequest request = createBookRequest(authorPublicIds);

        // When
        BookDto result = bookService.addBook(request);

        // Then
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        Book savedBook = bookCaptor.getValue();

        assertThat(savedBook.getPublicId()).isEqualTo(SAMPLE_PUBLIC_ID);
        assertThat(savedBook.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedBook.getAuthors()).containsExactlyElementsOf(authors);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(SAMPLE_PUBLIC_ID);
    }

    @Test
    void addBook_shouldThrowExceptionWhenIsbnExists() {
        // Given
        Book existingBook = createBook();
        when(authorRepository.findByPublicIdIn(any())).thenReturn(existingBook.getAuthors());
        when(bookRepository.findBookByIsbn(SAMPLE_ISBN)).thenReturn(existingBook);

        CreateBookRequest request = createBookRequest(List.of(UUID.randomUUID().toString()));

        // When / Then
        assertThatThrownBy(() -> bookService.addBook(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Book with the provided ISBN already exists.");
    }

    @Test
    void getBookByPublicId_shouldReturnBook() {
        // Given
        Book book = createBook();
        when(bookRepository.findBookByPublicId(SAMPLE_PUBLIC_ID)).thenReturn(book);

        // When
        BookDto result = bookService.getBookByPublicId(SAMPLE_PUBLIC_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(SAMPLE_PUBLIC_ID);
        assertThat(result.getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    void getBookByPublicId_shouldThrowExceptionWhenNotFound() {
        // Given
        when(bookRepository.findBookByPublicId(SAMPLE_PUBLIC_ID)).thenReturn(null);

        // When / Then
        assertThatThrownBy(() -> bookService.getBookByPublicId(SAMPLE_PUBLIC_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book not found");
    }

    @Test
    void getBooks_shouldReturnListOfBooks() {
        // Given
        GetBookParams params = createGetBookParams();
        Pageable pageable = mock(Pageable.class);
        Specification<Book> specification = mock(Specification.class);
        List<Book> books = List.of(createBook(), createBook());
        when(persistenceUtil.buildPageable(params.getSize(), params.getPage(), params.getOrderBy(), params.getOrderDirection())).thenReturn(pageable);
        when(persistenceUtil.buildBookListSpecification(params)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(books);

        // When
        List<BookDto> result = bookService.getBooks(params);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(books.size());
        verify(persistenceUtil).buildPageable(params.getSize(), params.getPage(), params.getOrderBy(), params.getOrderDirection());
        verify(persistenceUtil).buildBookListSpecification(params);
        verify(bookRepository).findAll(specification, pageable);
    }

    @Test
    void getBooks_shouldThrowExceptionWhenNoBooksFound() {
        // Given
        GetBookParams params = createGetBookParams();
        Pageable pageable = mock(Pageable.class);
        Specification<Book> specification = mock(Specification.class);
        when(persistenceUtil.buildPageable(params.getSize(), params.getPage(), params.getOrderBy(), params.getOrderDirection())).thenReturn(pageable);
        when(persistenceUtil.buildBookListSpecification(params)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(List.of());

        // When / Then
        assertThatThrownBy(() -> bookService.getBooks(params))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No books found for the provided criteria.");
    }

    @Test
    void changeBookAmount_shouldUpdateBookAmount() {
        // Given
        int amount = 5;
        when(bookRepository.changeBookAmount(SAMPLE_PUBLIC_ID, amount)).thenReturn(1);
        when(bookRepository.findBookByPublicId(SAMPLE_PUBLIC_ID)).thenReturn(createBook());

        // When
        BookDto result = bookService.changeBookAmount(SAMPLE_PUBLIC_ID, amount);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(SAMPLE_PUBLIC_ID);
        verify(bookRepository).changeBookAmount(SAMPLE_PUBLIC_ID, amount);
        verify(bookRepository).findBookByPublicId(SAMPLE_PUBLIC_ID);
    }

    @Test
    void changeBookAmount_shouldThrowExceptionWhenBookNotFound() {
        // Given
        int amount = 5;
        when(bookRepository.changeBookAmount(SAMPLE_PUBLIC_ID, amount)).thenReturn(0);

        // When / Then
        assertThatThrownBy(() -> bookService.changeBookAmount(SAMPLE_PUBLIC_ID, amount))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book not found or amount is incorrect");
        verify(bookRepository).changeBookAmount(SAMPLE_PUBLIC_ID, amount);
    }



    private Book createBook() {
        return Book.builder()
                .publicId(SAMPLE_PUBLIC_ID)
                .title("Sample Book")
                .isbn(SAMPLE_ISBN)
                .amount(1)
                .publicationYear(2022)
                .publisher("Sample Publisher")
                .authors(List.of(createAuthor(UUID.randomUUID().toString())))
                .build();
    }

    private Author createAuthor(String publicId) {
        return Author.builder()
                .publicId(publicId)
                .name("Author Name")
                .birthYear(1970)
                .build();
    }

    private CreateBookRequest createBookRequest(List<String> authorPublicIds) {
        return CreateBookRequest.builder()
                .title("New Book")
                .isbn(SAMPLE_ISBN)
                .publicationYear(2022)
                .publisher("Test Publisher")
                .authorPublicIds(authorPublicIds)
                .build();
    }

    private GetBookParams createGetBookParams() {
        return GetBookParams.builder()
                .size(10)
                .page(1)
                .orderBy("title")
                .orderDirection("asc")
                .build();
    }

}

