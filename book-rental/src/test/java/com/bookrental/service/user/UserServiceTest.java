package com.bookrental.service.user;

import com.bookrental.api.user.request.EditUserRequest;
import com.bookrental.api.user.request.GetUserAccountParams;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.PersistenceUtil;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.UserRepository;
import com.bookrental.security.SecurityUtil;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PersistenceUtil persistenceUtil;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private UserService userService;

    private static final String EMAIL = "john.doe@example.com";
    private static final String PUBLIC_ID = "12345";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "new_password";
    private static final String ROLE_BASIC = "basic";
    private static final String UPDATED_FIRST_NAME = "UpdatedFirstName";
    private static final String UPDATED_LAST_NAME = "UpdatedLastName";

    private EditUserRequest editUserRequest;

    @BeforeEach
    void setUp() {
        editUserRequest = EditUserRequest.builder()
                .currentPassword(PASSWORD)
                .newPassword(NEW_PASSWORD)
                .firstName(UPDATED_FIRST_NAME)
                .lastName(UPDATED_LAST_NAME)
                .build();
    }

    @Test
    void createUser_WhenEmailAlreadyExists_ThrowsBadRequestException() {
        // Given
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(createTestUserDto());
        });
        assertEquals("Email is already in use", exception.getMessage());
    }

    @Test
    void createUser_WhenEmailDoesNotExist_CreatesUser() {
        // Given
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        UserDto result = userService.createUser(createTestUserDto());

        // Then
        assertNotNull(result);
        assertEquals(EMAIL, result.getEmail());
        assertEquals(FIRST_NAME, result.getFirstName());
        assertEquals(LAST_NAME, result.getLastName());
        assertEquals(ROLE_BASIC, result.getRole());
        assertNotNull(result.getPublicId());
        assertNotNull(result.getPassword());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserByEmail_WhenUserExists_ReturnsUserDto() {
        // Given
        User user = createTestUser();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // When
        UserDto result = userService.getUserByEmail(EMAIL);

        // Then
        assertNotNull(result);
        assertEquals(EMAIL, result.getEmail());
    }

    @Test
    void getUserByEmail_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByEmail(EMAIL);
        });
        assertEquals("Failed to obtain user by email", exception.getMessage());
    }

    @Test
    void getUserByPublicId_WhenUserExists_ReturnsUserDto() {
        // Given
        User user = createTestUser();

        when(userRepository.getByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));

        // When
        UserDto result = userService.getUserByPublicId(PUBLIC_ID);

        // Then
        assertNotNull(result);
        assertEquals(PUBLIC_ID, result.getPublicId());
        assertEquals(EMAIL, result.getEmail());
    }

    @Test
    void getUserByPublicId_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.getByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByPublicId(PUBLIC_ID);
        });
        assertEquals("Failed to obtain user by publicId", exception.getMessage());
    }

    @Test
    void getUsers_WhenUsersExist_ReturnsUserDtoList() {
        // Given
        User user = createTestUser();
        GetUserAccountParams params = createGetUserAccountParams();
        when(persistenceUtil.buildPageable(params.getSize(), params.getPage(), params.getOrderBy(), params.getOrderDirection()))
                .thenReturn(PageRequest.of(params.getPage(), params.getSize()));
        when(persistenceUtil.buildUserListSpecification(params)).thenReturn(getSpecification());
        when(userRepository.findAll(any(getSpecification().getClass()), any(Pageable.class)))
                .thenReturn(List.of(user));

        // When
        List<UserDto> result = userService.getUsers(params);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PUBLIC_ID, result.get(0).getPublicId());
    }

    @Test
    void getUsers_WhenUsersDoNotExist_ThrowsResourceNotFoundException() {
        // Given
        GetUserAccountParams params = createGetUserAccountParams();
        when(persistenceUtil.buildPageable(params.getSize(), params.getPage(), params.getOrderBy(), params.getOrderDirection()))
                .thenReturn(PageRequest.of(params.getPage(), params.getSize()));
        when(persistenceUtil.buildUserListSpecification(params)).thenReturn(getSpecification());
        when(userRepository.findAll(any(getSpecification().getClass()), any(Pageable.class)))
                .thenReturn(List.of());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUsers(params);
        });
        assertEquals("Users not found", exception.getMessage());
    }

    @Test
    void editUser_WhenPasswordIsIncorrect_ThrowsForbiddenException() {
        // Given
        User user = createTestUser();
        when(userRepository.getByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));
        when(securityUtil.checkIfPasswordMatches(editUserRequest.getCurrentPassword(), user.getPassword())).thenReturn(false);

        // When & Then
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            userService.editUser(PUBLIC_ID, editUserRequest);
        });
        assertEquals("Current password is incorrect.", exception.getMessage());
    }

    @Test
    void editUser_WhenPasswordMatches_UpdatesUser() throws ForbiddenException {
        // Given
        User user = createTestUser();
        when(userRepository.getByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));
        when(securityUtil.checkIfPasswordMatches(PASSWORD, PASSWORD)).thenReturn(true);
        when(securityUtil.encryptPassword(NEW_PASSWORD)).thenReturn(NEW_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserDto result = userService.editUser(PUBLIC_ID, editUserRequest);

        // Then
        assertNotNull(result);
        assertEquals(UPDATED_FIRST_NAME, result.getFirstName());
        assertEquals(UPDATED_LAST_NAME, result.getLastName());
        assertEquals(NEW_PASSWORD, result.getPassword());
    }

    @Test
    void editUser_WhenPasswordIsIncorrectButUserIsAdmin_UpdatesUser() throws ForbiddenException {
        // Given
        User user = createTestUser();
        when(userRepository.getByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));
        when(securityUtil.encryptPassword(NEW_PASSWORD)).thenReturn(NEW_PASSWORD);
        when(securityUtil.checkIfAdmin(PUBLIC_ID)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserDto result = userService.editUser(PUBLIC_ID, editUserRequest);

        // Then
        assertNotNull(result);
        assertEquals(UPDATED_FIRST_NAME, result.getFirstName());
        assertEquals(UPDATED_LAST_NAME, result.getLastName());
        assertEquals(NEW_PASSWORD, result.getPassword());
    }

    @Test
    void toggleUserDeletedStatus_WhenUserExists_ReturnsUpdatedUser() {
        // Given
        User user = createTestUser();
        when(userRepository.getByPublicId(PUBLIC_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserDto result = userService.toggleUserDeletedStatus(PUBLIC_ID);

        // Then
        assertNotNull(result);
        assertTrue(result.isDeleted());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void toggleUserDeletedStatus_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.getByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.toggleUserDeletedStatus(PUBLIC_ID);
        });
        assertEquals("Failed to obtain user by publicId", exception.getMessage());
    }

    private User createTestUser() {
        return User.builder()
                .publicId(PUBLIC_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .role(ROLE_BASIC)
                .deleted(false)
                .build();
    }

    private UserDto createTestUserDto() {
        return UserDto.builder()
                .publicId(PUBLIC_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .role(ROLE_BASIC)
                .build();
    }

    private GetUserAccountParams createGetUserAccountParams() {
        return GetUserAccountParams.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .role(ROLE_BASIC)
                .deleted(false)
                .page(0)
                .size(10)
                .orderBy("firstName")
                .orderDirection("asc")
                .build();
    }

    private Specification<User> getSpecification() {
        return (root, query, criteriaBuilder) -> null;
    }

}
