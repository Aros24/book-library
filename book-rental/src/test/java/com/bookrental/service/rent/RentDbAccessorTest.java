package com.bookrental.service.rent;

import com.bookrental.persistence.RentDbAccessor;
import com.bookrental.persistence.entity.Book;
import com.bookrental.persistence.entity.Rent;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.RentRepository;
import com.bookrental.service.ServiceUtil;
import com.bookrental.service.book.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentDbAccessorTest {

    @Mock
    private RentRepository rentRepository;

    @Mock
    private BookService bookService;

    @Mock
    private ServiceUtil serviceUtil;

    @InjectMocks
    private RentDbAccessor rentDbAccessor;

    @Test
    void finishRentCreation_ShouldCreateAndSaveRent() {
        // Given
        User user = new User();
        user.setPublicId("userPublicId");
        Book book = new Book();
        book.setPublicId("bookPublicId");

        when(serviceUtil.generateRandomUUID()).thenReturn("randomUUID");
        when(rentRepository.save(any(Rent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rent rent = rentDbAccessor.finishRentCreation(user, book);

        // Then
        assertNotNull(rent);
        verify(bookService, times(1)).changeBookAmount(book.getPublicId(), -1);
        verify(rentRepository, times(1)).save(any(Rent.class));
    }

}