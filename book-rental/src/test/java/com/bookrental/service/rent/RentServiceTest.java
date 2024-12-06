package com.bookrental.service.rent;

import com.bookrental.api.rent.request.GetRentParams;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.PersistenceUtil;
import com.bookrental.persistence.RentDbAccessor;
import com.bookrental.persistence.constants.RentConstants;
import com.bookrental.persistence.entity.Author;
import com.bookrental.persistence.entity.Book;
import com.bookrental.persistence.entity.Rent;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.RentRepository;
import com.bookrental.service.book.BookService;
import com.bookrental.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentServiceTest {

    @Mock
    private RentRepository rentRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private PersistenceUtil persistenceUtil;

    @Mock
    private RentDbAccessor rentDbAccessor;

    @InjectMocks
    private RentService rentService;

    private static final String RENT_PUBLIC_ID = "rent12345";
    private static final String USER_PUBLIC_ID = "user12345";
    private static final String BOOK_PUBLIC_ID = "book12345";
    private static final String BOOK_TITLE = "The Great Gatsby";
    private static final String USER_EMAIL = "john.doe@example.com";

    private Rent rent;

    @BeforeEach
    void setUp() {
        rent = Rent.builder()
                .publicId(RENT_PUBLIC_ID)
                .userPublicId(USER_PUBLIC_ID)
                .bookPublicId(BOOK_PUBLIC_ID)
                .startDate(LocalDateTime.now())
                .book(createTestBook())
                .endDate(null)
                .build();
    }

    @Test
    void createRent_WhenBookCannotBeRented_ThrowsBadRequestException() {
        // Given
        when(userService.getUserRawEntityByPublicId(USER_PUBLIC_ID)).thenReturn(createTestUser());
        when(bookService.getBookRawEntityByPublicId(BOOK_PUBLIC_ID)).thenReturn(createTestBook());
        when(rentDbAccessor.finishRentCreation(any(), any())).thenThrow(new BadRequestException("Book cannot be rented!"));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            rentService.createRent(BOOK_PUBLIC_ID, USER_PUBLIC_ID);
        });
        assertEquals("Book cannot be rented!", exception.getMessage());
    }

    @Test
    void createRent_WhenSuccessful_CreatesRent() {
        // Given
        when(userService.getUserRawEntityByPublicId(USER_PUBLIC_ID)).thenReturn(createTestUser());
        when(bookService.getBookRawEntityByPublicId(BOOK_PUBLIC_ID)).thenReturn(createTestBook());
        when(rentDbAccessor.finishRentCreation(any(), any())).thenReturn(rent);

        // When
        RentDto result = rentService.createRent(BOOK_PUBLIC_ID, USER_PUBLIC_ID);

        // Then
        assertNotNull(result);
        assertEquals(RENT_PUBLIC_ID, result.getPublicId());
        assertEquals(USER_PUBLIC_ID, result.getUserPublicId());
        assertEquals(BOOK_PUBLIC_ID, result.getBookPublicId());
    }

    @Test
    void getRents_WhenRentsExist_ReturnsRentDtoList() {
        // Given
        GetRentParams params = createGetRentParams();
        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());
        Specification<Rent> specification = mock(Specification.class);
        when(persistenceUtil.buildPageable(params.getSize(), params.getPage(), RentConstants.START_DATE.getValue(), "DESC"))
                .thenReturn(pageable);
        when(persistenceUtil.buildRentListSpecification(USER_PUBLIC_ID)).thenReturn(specification);
        when(rentRepository.findAll(specification, pageable)).thenReturn(List.of(rent));

        // When
        List<RentDto> result = rentService.getRents(USER_PUBLIC_ID, params);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RENT_PUBLIC_ID, result.get(0).getPublicId());
    }

    @Test
    void getRents_WhenNoRentsExist_ThrowsResourceNotFoundException() {
        // Given
        GetRentParams params = createGetRentParams();
        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());
        Specification<Rent> specification = mock(Specification.class);
        when(persistenceUtil.buildPageable(params.getSize(), params.getPage(), RentConstants.START_DATE.getValue(), "DESC"))
                .thenReturn(pageable);
        when(persistenceUtil.buildRentListSpecification(USER_PUBLIC_ID)).thenReturn(specification);
        when(rentRepository.findAll(specification, pageable)).thenReturn(List.of());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            rentService.getRents(USER_PUBLIC_ID, params);
        });
        assertEquals("Rents not found for the provided criteria", exception.getMessage());
    }

    @Test
    void endRent_WhenRentExists_EndsRentSuccessfully() {
        // Given
        when(rentRepository.findByPublicId(RENT_PUBLIC_ID)).thenReturn(Optional.of(rent));
        when(bookService.changeBookAmount(BOOK_PUBLIC_ID, 1)).thenReturn(null);

        // When
        RentDto result = rentService.endRent(RENT_PUBLIC_ID);

        // Then
        assertNotNull(result);
        assertNotNull(result.getEndDate());
        verify(rentRepository, times(1)).save(any(Rent.class));
    }

    @Test
    void endRent_WhenRentDoesNotExist_ThrowsResourceNotFoundException() {
        // Given
        when(rentRepository.findByPublicId(RENT_PUBLIC_ID)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            rentService.endRent(RENT_PUBLIC_ID);
        });
        assertEquals("Rent not found", exception.getMessage());
    }

    private User createTestUser() {
        return User.builder()
                .publicId(USER_PUBLIC_ID)
                .email(USER_EMAIL)
                .build();
    }

    private Book createTestBook() {
        return Book.builder()
                .publicId(BOOK_PUBLIC_ID)
                .title(BOOK_TITLE)
                .authors(createAuthors())
                .build();
    }

    private GetRentParams createGetRentParams() {
        return GetRentParams.builder()
                .size(10)
                .page(0)
                .build();
    }

    private List<Author> createAuthors() {
        return List.of(
                Author.builder().name("F. Scott Fitzgerald").build(),
                Author.builder().name("Some Other Author").build()
        );
    }

}
