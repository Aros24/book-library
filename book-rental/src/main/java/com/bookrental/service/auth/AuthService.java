package com.bookrental.service.auth;

import com.bookrental.api.auth.request.LoginRequest;
import com.bookrental.api.auth.request.RegisterRequest;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.security.SecurityUtil;
import com.bookrental.security.jwt.JwtUtil;
import com.bookrental.service.user.UserDto;
import com.bookrental.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final JwtUtil jwtUtil;
    private final String BASIC_ROLE = "basic";

    @Autowired
    public AuthService(UserService userService, SecurityUtil securityUtil, JwtUtil jwtUtil) {
        this.userService = userService;
        this.securityUtil = securityUtil;
        this.jwtUtil = jwtUtil;
    }

    public UserDto registerUser(RegisterRequest request) {
        UserDto newUser = UserDto.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(securityUtil.encryptPassword(request.getPassword()))
                .role(BASIC_ROLE)
                .build();

        return userService.createUser(newUser);
    }

    public LoginDto loginUser(LoginRequest request) throws ForbiddenException {
        UserDto userDto = userService.getUserByEmail(request.getEmail());
        if (!securityUtil.checkIfPasswordMatches(request.getPassword(), userDto.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }
        if (userDto.isDeleted()) {
            throw new ForbiddenException("Account has been deleted");
        }

        return LoginDto.builder()
                .publicId(userDto.getPublicId())
                .role(userDto.getRole())
                .jwt(jwtUtil.generateToken(userDto.getEmail(), userDto.getPublicId()))
                .build();
    }

}
