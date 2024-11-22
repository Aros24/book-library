package com.bookrental.api.user;

import com.bookrental.api.user.response.User;
import com.bookrental.service.user.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User mapUserDtoToUser(UserDto user) {
        return User.builder()
                .publicId(user.getPublicId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .deleted(user.isDeleted())
                .build();
    }

}
