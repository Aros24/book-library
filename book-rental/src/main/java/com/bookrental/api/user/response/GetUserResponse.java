package com.bookrental.api.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetUserResponse {

    @Schema(description = "Unique public ID of the user in UUID form")
    String publicId;

    @Schema(description = "User's first name", example = "John")
    String firstName;

    @Schema(description = "User's last name", example = "Doe")
    String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    String email;

    @Schema(description = "Role of the user (e.g., admin, basic)", example = "basic")
    String role;

    @Schema(description = "Is user deleted")
    boolean deleted;

}
