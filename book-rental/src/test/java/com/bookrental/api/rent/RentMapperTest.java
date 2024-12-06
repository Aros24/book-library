package com.bookrental.api.rent;

import com.bookrental.api.rent.response.Rent;
import com.bookrental.persistence.entity.Author;
import com.bookrental.service.rent.RentDto;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RentMapperTest {

    private final RentMapper rentMapper = new RentMapper();

    @Test
    void mapRentDtoToRent_ShouldMapCorrectly() throws IllegalAccessException {
        // Given
        RentDto rentDto = RentDto.builder()
                .publicId("67890")
                .userPublicId("12345")
                .bookPublicId("54321")
                .bookTitle("Test Book Title")
                .bookAuthors(List.of(Author.builder()
                        .name("F. Scott Fitzgerald")
                        .build().getName()))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        // When
        Rent rent = rentMapper.mapRentDtoToRent(rentDto);

        // Then
        assertNotNull(rent);
        assertEquals(rentDto.getPublicId(), rent.getPublicId());
        assertEquals(rentDto.getUserPublicId(), rent.getUserPublicId());
        assertEquals(rentDto.getBookPublicId(), rent.getBookPublicId());
        assertEquals(rentDto.getBookTitle(), rent.getBookTitle());
        assertEquals(rentDto.getBookAuthors(), rent.getBookAuthors());
        assertEquals(rentDto.getStartDate(), rent.getStartDate());
        assertEquals(rentDto.getEndDate(), rent.getEndDate());

        // Check all fields of Rent class for null values
        for (Field field : Rent.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(rent);
            assertNotNull(value, "Field '" + field.getName() + "' should not be null");
        }
    }

    @Test
    void mapRentDtoToRent_ShouldHandleNullFieldsGracefully() {
        // Given
        RentDto rentDto = RentDto.builder()
                .publicId(null)
                .userPublicId(null)
                .bookPublicId(null)
                .bookTitle(null)
                .bookAuthors(null)
                .startDate(null)
                .endDate(null)
                .build();

        // When
        Rent rent = rentMapper.mapRentDtoToRent(rentDto);

        // Then
        assertNotNull(rent);
        assertNull(rent.getPublicId());
        assertNull(rent.getUserPublicId());
        assertNull(rent.getBookPublicId());
        assertNull(rent.getBookTitle());
        assertNull(rent.getBookAuthors());
        assertNull(rent.getStartDate());
        assertNull(rent.getEndDate());
    }

}