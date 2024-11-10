package com.bookrental.api.user;

import com.bookrental.api.user.response.GetUserResponse;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.UserRepository;
import com.bookrental.service.user.UserDto;
import com.bookrental.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{publicId}")
    public GetUserResponse getUserByPublicId(@PathVariable("publicId") String publicID) {
        UserDto user = userService.getUserByPublicId(publicID);
        return GetUserResponse.builder()
                .publicId(user.getPublicId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

}
