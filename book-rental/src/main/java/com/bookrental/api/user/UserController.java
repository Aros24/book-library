package com.bookrental.api.user;

import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.UserRepository;
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

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<User> getUserByPublicId(@PathVariable("publicId") String publicID) {
        Optional<User> user = userRepository.getByPublicId(publicID);
        return user.map(ResponseEntity::ok).orElseThrow(() -> new ResourceNotFoundException(String.format("User not found for id: %s", publicID)));
    }

}
