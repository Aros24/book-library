package com.bookrental.persistence;

import com.bookrental.config.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersistenceUtilTest {

    private final PersistenceUtil persistenceUtil = new PersistenceUtil();

    private static final String NAME = "name";
    private static final String ORDER_BY = "asc";

    @Test
    void buildPageable_ValidInput_ShouldReturnPageable() {
        // Given
        int size = 10;
        int page = 1;

        // When
        Pageable pageable = persistenceUtil.buildPageable(size, page, NAME, ORDER_BY);

        // Then
        assertNotNull(pageable);
        assertEquals(PageRequest.of(page, size, org.springframework.data.domain.Sort.by(NAME).ascending()), pageable);
    }

    @Test
    void buildPageable_InvalidSize_ShouldThrowBadRequestException() {
        // Given
        int invalidSize = 0;

        // When & Then
        assertThrows(BadRequestException.class, () -> persistenceUtil.buildPageable(invalidSize, 1, NAME, ORDER_BY));
    }

    @Test
    void buildPageable_InvalidPage_ShouldThrowBadRequestException() {
        // Given
        int invalidPage = -1;

        // When & Then
        assertThrows(BadRequestException.class, () -> persistenceUtil.buildPageable(10, invalidPage, NAME, ORDER_BY));
    }

}
