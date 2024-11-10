package com.bookrental.service.user;

import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .role("basic")
                .build();
    }

    @Test
    void createUser_WhenEmailAlreadyExists_ThrowsBadRequestException() {
        // Given
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(userDto);
        });
        assertEquals("Email is already in use", exception.getMessage());
    }

    @Test
    void createUser_WhenEmailDoesNotExist_CreatesUser() {
        // Given
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        UserDto result = userService.createUser(userDto);

        // Then
        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getFirstName(), result.getFirstName());
        assertEquals(userDto.getLastName(), result.getLastName());
        assertEquals(userDto.getRole(), result.getRole());
        assertNotNull(result.getPublicId());
        assertNotNull(result.getPassword());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserByEmail_WhenUserExists_ReturnsUserDto() {
        // Given
        User user = createTestUser("john.doe@example.com");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        // When
        UserDto result = userService.getUserByEmail(userDto.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void getUserByEmail_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByEmail(userDto.getEmail());
        });
        assertEquals("Failed to obtain user by email", exception.getMessage());
    }

    @Test
    void getUserByPublicId_WhenUserExists_ReturnsUserDto() {
        // Given
        User user = createTestUser("john.doe@example.com");

        when(userRepository.getByPublicId("12345")).thenReturn(Optional.of(user));

        // When
        UserDto result = userService.getUserByPublicId("12345");

        // Then
        assertNotNull(result);
        assertEquals(user.getPublicId(), result.getPublicId());
    }

    @Test
    void getUserByPublicId_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.getByPublicId("12345")).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByPublicId("12345");
        });
        assertEquals("Failed to obtain user by publicId", exception.getMessage());
    }

    private User createTestUser(String email) {
        User user = new User();
        user.setPublicId("12345");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail(email);
        user.setPassword("password");
        user.setRole("basic");
        user.setCreatedAt(Instant.now().atZone(java.time.ZoneOffset.UTC).toLocalDateTime());
        return user;
    }
}
