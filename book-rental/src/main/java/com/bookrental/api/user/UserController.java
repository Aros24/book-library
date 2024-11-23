package com.bookrental.api.user;

import com.bookrental.api.user.request.GetUserAccountParams;
import com.bookrental.api.user.response.User;
import com.bookrental.config.exceptions.ErrorResponse;
import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.security.SecurityConfig;
import com.bookrental.service.user.UserDto;
import com.bookrental.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User", description = "User related endpoints")
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{publicId}")
    @Operation(
            summary = "Get user details by public ID",
            description = "Fetches the details of a user based on the provided public ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details successfully fetched",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "User is not authorized to access the details of this user",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public User getUserByPublicId(@PathVariable("publicId") String publicID) throws ForbiddenException {
        SecurityConfig.checkUserAccess(publicID);
        UserDto user = userService.getUserByPublicId(publicID);
        return userMapper.mapUserDtoToUser(user);
    }

    @GetMapping("/accounts")
    @Operation(summary = "Get users with optional filters", description = "Retrieve a paginated list of users based on optional query parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Users not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<User> getUsers(GetUserAccountParams params) {
        List<UserDto> users = userService.getUsers(params);
        return users.stream()
                .map(userMapper::mapUserDtoToUser)
                .toList();
    }

}
