package com.bookrental.service.user;

import com.bookrental.api.user.request.GetUserAccountParams;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        Pageable pageable = buildPageable(params);
        Specification<User> specification = buildSpecification(params);

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

    private Pageable buildPageable(GetUserAccountParams params) {
        if (params.getSize() == null || params.getSize() < 1) {
            throw new BadRequestException("Invalid size");
        }
        if (params.getPage() == null || params.getPage() < 0) {
            throw new BadRequestException("Invalid page number");
        }
        String orderBy = params.getOrderBy() != null ? params.getOrderBy() : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(params.getOrderDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(params.getPage(), params.getSize(), Sort.by(direction, orderBy));
    }

    private Specification<User> buildSpecification(GetUserAccountParams params) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<>();

            if (params.getFirstName() != null) {
                predicates.add(cb.like(root.get("firstName"), "%" + params.getFirstName() + "%"));
            }
            if (params.getLastName() != null) {
                predicates.add(cb.like(root.get("lastName"), "%" + params.getLastName()+ "%"));
            }
            if (params.getEmail() != null) {
                predicates.add(cb.like(root.get("email"), "%" + params.getEmail() + "%"));
            }
            if (params.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), params.getRole()));
            }
            if (params.getDeleted() != null) {
                predicates.add(cb.equal(root.get("deleted"), params.getDeleted()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
