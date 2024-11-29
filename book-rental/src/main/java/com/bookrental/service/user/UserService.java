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
import com.bookrental.service.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PersistenceUtil persistenceUtil;
    private final SecurityUtil securityUtil;
    private final ServiceUtil serviceUtil;

    @Autowired
    public UserService(UserRepository userRepository, PersistenceUtil persistenceUtil,
                       SecurityUtil securityUtil, ServiceUtil serviceUtil) {
        this.userRepository = userRepository;
        this.persistenceUtil = persistenceUtil;
        this.securityUtil = securityUtil;
        this.serviceUtil = serviceUtil;
    }

    public UserDto createUser(UserDto userDto) {
        if (checkIfUserExists(userDto.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        userDto = userDto.toBuilder()
                .publicId(serviceUtil.generateRandomUUID())
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

        userRepository.save(newUser);
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
        return buildUser(getUserByPublicIdFromDb(publicId));
    }

    public List<UserDto> getUsers(GetUserAccountParams params) {
        Pageable pageable = persistenceUtil.buildPageable(params.getSize(), params.getPage(), params.getOrderBy(), params.getOrderDirection());
        Specification<User> specification = persistenceUtil.buildUserListSpecification(params);

        List<UserDto> userDtoList = userRepository.findAll(specification, pageable).stream()
                .map(this::buildUser)
                .toList();

        if (userDtoList.isEmpty()) {
            throw new ResourceNotFoundException("Users not found for the provided criteria");
        }

        return userDtoList;
    }

    public UserDto editUser(String publicId, EditUserRequest editUserRequest) throws ForbiddenException {
        User user = getUserByPublicIdFromDb(publicId);

        if (!securityUtil.checkIfAdmin(publicId) &&
                !securityUtil.checkIfPasswordMatches(editUserRequest.getCurrentPassword(), user.getPassword())) {
            throw new ForbiddenException("Current password is incorrect.");
        }

        if (editUserRequest.getFirstName() != null) {
            user.setFirstName(editUserRequest.getFirstName());
        }
        if (editUserRequest.getLastName() != null) {
            user.setLastName(editUserRequest.getLastName());
        }
        if (editUserRequest.getNewPassword() != null) {
            user.setPassword(securityUtil.encryptPassword(editUserRequest.getNewPassword()));
        }

        return buildUser(userRepository.save(user));
    }

    public UserDto toggleUserDeletedStatus(String publicId) {
        User user = getUserByPublicIdFromDb(publicId);
        user.setDeleted(!user.getDeleted());
        return buildUser(userRepository.save(user));
    }

    private User getUserByPublicIdFromDb(String publicId) {
        Optional<User> user = userRepository.getByPublicId(publicId);
        if (user.isPresent()) {
            return user.get();
        }
        throw new ResourceNotFoundException("Failed to obtain user by publicId");
    }

    private boolean checkIfUserExists(String email) {
        return userRepository.existsByEmail(email);
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
