package com.bookrental.security.jwt;

import com.bookrental.config.exceptions.UnauthorizedException;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    private static final String PUBLIC_ID = "12345";
    private static final String EMAIL = "test@example.com";
    private static final String EXPIRATION = "expiration";
    private static final String REFRESH_TIME = "refreshTime";
    private static final String SECRET = "secret";

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        setPrivateField(jwtUtil, SECRET, "One day I will for sure use AWS to keep necessary secrets :)");
        setPrivateField(jwtUtil, EXPIRATION, 1000 * 60 * 15L); // 15 minutes
        setPrivateField(jwtUtil, REFRESH_TIME, 1000 * 60 * 2L); // 2 minutes
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // Given & When
        String token = jwtUtil.generateToken(EMAIL, PUBLIC_ID);

        // Then
        assertNotNull(token);
    }

    @Test
    void validateRawToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Given
        String token = jwtUtil.generateToken(EMAIL, PUBLIC_ID);

        // When & Then
        assertTrue(jwtUtil.validateRawToken(token));
    }

    @Test
    void isTokenExpiringSoon_ShouldReturnTrue_WhenTokenIsCloseToExpiration() throws Exception {
        // Given
        setPrivateField(jwtUtil, EXPIRATION, 1000 * 60 * 2L); // 2 minutes expiration
        String token = jwtUtil.generateToken(EMAIL, PUBLIC_ID);

        // When & Then
        assertTrue(jwtUtil.isTokenExpiringSoon(token));
    }

    @Test
    void validateRawToken_ShouldReturnFalse_WhenTokenIsExpired() {
        // Given
        String token = jwtUtil.generateToken(EMAIL, PUBLIC_ID);
        String expiredToken = token.substring(0, token.length() - 1) + "123456"; // corrupt signature

        // When & Then
        assertFalse(jwtUtil.validateRawToken(expiredToken));
    }

    @Test
    void extractEmailFromRawToken_ShouldReturnEmail() {
        // Given
        String token = jwtUtil.generateToken(EMAIL, PUBLIC_ID);

        // When
        String extractedEmail = jwtUtil.extractEmailFromRawToken(token);

        // Then
        assertEquals(EMAIL, extractedEmail);
    }

    @Test
    void extractPublicIdFromRawToken_ShouldReturnPublicId() {
        // Given
        String token = jwtUtil.generateToken(EMAIL, PUBLIC_ID);

        // When
        String extractedPublicId = jwtUtil.extractPublicIdFromRawToken(token);

        // Then
        assertEquals(PUBLIC_ID, extractedPublicId);
    }

    @Test
    void getJwtFromRequest_ShouldReturnNull_WhenAuthorizationHeaderIsMissing() {
        // Given
        when(request.getHeader(SecurityConstants.AUTHORIZATION.getValue())).thenReturn(null);

        // When
        String token = jwtUtil.getJwtFromRequest(request);

        // Then
        assertNull(token);
    }

    @Test
    void getJwtFromRequest_ShouldReturnToken_WhenAuthorizationHeaderIsValid() {
        // Given
        String bearerToken = "Bearer " + jwtUtil.generateToken(EMAIL, PUBLIC_ID);
        when(request.getHeader(SecurityConstants.AUTHORIZATION.getValue())).thenReturn(bearerToken);

        // When & Then
        assertNotNull(jwtUtil.getJwtFromRequest(request));
    }

    @Test
    void getJwtFromRequest_ShouldReturnNull_WhenAuthorizationHeaderIsMalformed() {
        // Given
        String malformedBearerToken = "MalformedTokenWithoutBearerPrefix";
        when(request.getHeader(SecurityConstants.AUTHORIZATION.getValue())).thenReturn(malformedBearerToken);

        // When & Then
        assertNull(jwtUtil.getJwtFromRequest(request));
    }

    @Test
    void isTokenExpiringSoon_ShouldReturnFalse () {
        // Given
        String token = jwtUtil.generateToken(EMAIL, PUBLIC_ID);

        // When & Then
        assertFalse(jwtUtil.isTokenExpiringSoon(token));
    }

    @Test
    void getTokenFromSession_ShouldThrowUnauthorizedException_WhenUserIsNotAuthenticated() {
        // Given & When & Then
        assertThrows(UnauthorizedException.class, () -> jwtUtil.getTokenFromSession(PUBLIC_ID));
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
