package com.bookrental.service.auth;

import com.bookrental.api.auth.request.LoginRequest;
import com.bookrental.api.auth.request.RegisterRequest;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.security.jwt.JwtUtil;
import com.bookrental.service.user.UserDto;
import com.bookrental.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_ShouldReturnNewUser_WhenRequestIsValid() {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .build();
        UserDto expectedUserDto = createUserDto();

        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userService.createUser(any(UserDto.class))).thenReturn(expectedUserDto);

        // When
        UserDto result = authService.registerUser(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("basic", result.getRole());
        verify(passwordEncoder, times(1)).encode("password");
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void loginUser_ShouldReturnToken_WhenCredentialsAreValid() throws ForbiddenException {
        // Given
        LoginRequest loginRequest = createLoginRequest("john.doe@example.com", "password");
        UserDto userDto = createUserDto();

        String expectedToken = "test-jwt-token";

        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(userDto);
        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken(userDto.getEmail(), userDto.getPublicId())).thenReturn(expectedToken);

        // When
        String result = authService.loginUser(loginRequest).getJwt();

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
        verify(passwordEncoder, times(1)).matches("password", "encoded_password");
        verify(jwtUtil, times(1)).generateToken(userDto.getEmail(), userDto.getPublicId());
    }

    @Test
    void loginUser_ShouldThrowBadRequestException_WhenPasswordIsInvalid() {
        // Given
        LoginRequest loginRequest = createLoginRequest("john.doe@example.com", "wrong_password");
        UserDto userDto = createUserDto();

        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(userDto);
        when(passwordEncoder.matches(eq("wrong_password"), eq("encoded_password"))).thenReturn(false);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                authService.loginUser(loginRequest)
        );
        assertEquals("Invalid credentials", exception.getMessage());
        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
        verify(passwordEncoder, times(1)).matches("wrong_password", "encoded_password");
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .publicId("12345")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encoded_password")
                .role("basic")
                .build();
    }

    private LoginRequest createLoginRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

}
