package com.bookrental.service.user;

import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto userDto) {
        if (checkIfUserExists(userDto.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        userDto = userDto.toBuilder()
                .publicId(generateRandomUUID())
                .build();

        User newUser = User.builder()
                .publicId(userDto.getPublicId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .role(userDto.getRole())
                .build();

        try {
            userRepository.save(newUser);
        } catch (Exception e) {
            userRepository.deleteByPublicId(userDto.getPublicId());
            throw e;
        }

        return userDto;
    }

    public UserDto getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User dbUser = user.get();
            return UserDto.builder()
                    .publicId(dbUser.getPublicId())
                    .firstName(dbUser.getFirstName())
                    .lastName(dbUser.getLastName())
                    .email(dbUser.getEmail())
                    .password(dbUser.getPassword())
                    .role(dbUser.getRole())
                    .build();
        }

        throw new ResourceNotFoundException("Failed to obtain user by email");
    }

    private boolean checkIfUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private String generateRandomUUID() {
        return UUID.randomUUID().toString(); // length = 36
    }

}
