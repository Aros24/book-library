package com.bookrental.api.user;

import com.bookrental.api.user.response.User;
import com.bookrental.service.user.UserDto;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void mapUserDtoToUser_ShouldMapCorrectly() throws IllegalAccessException {
        // Given
        UserDto userDto = UserDto.builder()
                .publicId("12345")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role("basic")
                .deleted(false)
                .build();

        // When
        User user = userMapper.mapUserDtoToUser(userDto);

        // Then
        assertNotNull(user);
        assertEquals(userDto.getPublicId(), user.getPublicId());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getRole(), user.getRole());
        assertEquals(userDto.isDeleted(), user.isDeleted());

        for (Field field : User.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(user);
            assertNotNull(value, "Field '" + field.getName() + "' should not be null");
        }
    }

    @Test
    void mapUserDtoToUser_ShouldHandleNullFieldsGracefully() {
        // Given
        UserDto userDto = UserDto.builder()
                .publicId(null)
                .firstName(null)
                .lastName(null)
                .email(null)
                .role(null)
                .deleted(false)
                .build();

        // When
        User user = userMapper.mapUserDtoToUser(userDto);

        // Then
        assertNotNull(user);
        assertNull(user.getPublicId());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getEmail());
        assertNull(user.getRole());
        assertFalse(user.isDeleted());
    }

}