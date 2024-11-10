package com.bookrental.security.jwt;

import com.bookrental.security.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        setPrivateField(jwtUtil, "secret", "One day I will for sure use AWS to keep necessary secrets :)");
        setPrivateField(jwtUtil, "expiration", 1000 * 60 * 15L); // 15 minutes
        setPrivateField(jwtUtil, "refreshTime", 1000 * 60 * 2L); // 2 minutes
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // Given
        String email = "test@example.com";
        String publicId = "12345";

        // When
        String token = jwtUtil.generateToken(email, publicId);

        // Then
        assertNotNull(token);
    }

    @Test
    void validateRawToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Given
        String email = "test@example.com";
        String publicId = "12345";
        String token = jwtUtil.generateToken(email, publicId);

        // When
        boolean isValid = jwtUtil.validateRawToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateRawToken_ShouldReturnFalse_WhenTokenIsExpired() {
        // Given
        String email = "test@example.com";
        String publicId = "12345";
        String token = jwtUtil.generateToken(email, publicId);

        // Corrupt signature
        String expiredToken = token.substring(0, token.length() - 1) + "1";

        // When
        boolean isValid = jwtUtil.validateRawToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractEmailFromRawToken_ShouldReturnEmail() {
        // Given
        String email = "test@example.com";
        String publicId = "12345";
        String token = jwtUtil.generateToken(email, publicId);

        // When
        String extractedEmail = jwtUtil.extractEmailFromRawToken(token);

        // Then
        assertEquals(email, extractedEmail);
    }

    @Test
    void extractPublicIdFromRawToken_ShouldReturnPublicId() {
        // Given
        String email = "test@example.com";
        String publicId = "12345";
        String token = jwtUtil.generateToken(email, publicId);

        // When
        String extractedPublicId = jwtUtil.extractPublicIdFromRawToken(token);

        // Then
        assertEquals(publicId, extractedPublicId);
    }

    @Test
    void getJwtFromRequest_ShouldReturnToken_WhenAuthorizationHeaderIsValid() {
        // Given
        String bearerToken = "Bearer " + jwtUtil.generateToken("test@example.com", "12345");
        when(request.getHeader(SecurityConstants.AUTHORIZATION.getValue())).thenReturn(bearerToken);

        // When
        String token = jwtUtil.getJwtFromRequest(request);

        // Then
        assertNotNull(token);
    }

    @Test
    void isTokenExpiringSoon_ShouldReturnFalse () {
        // Given
        String email = "test@example.com";
        String publicId = "12345";
        String token = jwtUtil.generateToken(email, publicId);

        // When
        boolean isExpiringSoon = jwtUtil.isTokenExpiringSoon(token);

        // Then
        assertFalse(isExpiringSoon);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
