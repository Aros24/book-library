package com.bookrental.service.user;

import com.bookrental.api.user.request.GetUserAccountParams;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.PersistenceUtil;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PersistenceUtil persistenceUtil;

    @Autowired
    public UserService(UserRepository userRepository, PersistenceUtil persistenceUtil) {
        this.userRepository = userRepository;
        this.persistenceUtil = persistenceUtil;
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
                .deleted(false)
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
            return buildUser(dbUser);
        }
        throw new ResourceNotFoundException("Failed to obtain user by email");
    }

    public UserDto getUserByPublicId(String publicId) {
        Optional<User> user = userRepository.getByPublicId(publicId);
        if (user.isPresent()) {
            User dbUser = user.get();
            return buildUser(dbUser);
        }
        throw new ResourceNotFoundException("Failed to obtain user by publicId");
    }

    public List<UserDto> getUsers(GetUserAccountParams params) {
        Pageable pageable = persistenceUtil.buildPageable(params.getSize(), params.getPage(), params.getOrderBy(), params.getOrderDirection());
        Specification<User> specification = persistenceUtil.buildUserListSpecification(params);

        List<UserDto> userDtoList = userRepository.findAll(specification, pageable).stream()
                .map(this::buildUser)
                .toList();

        if (userDtoList.isEmpty()) {
            throw new ResourceNotFoundException("Users not found");
        }

        return userDtoList;
    }

    private boolean checkIfUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private String generateRandomUUID() {
        return UUID.randomUUID().toString(); // length = 36
    }

    private UserDto buildUser(User user) {
        return UserDto.builder()
                .publicId(user.getPublicId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .deleted(user.getDeleted())
                .build();
    }

}
