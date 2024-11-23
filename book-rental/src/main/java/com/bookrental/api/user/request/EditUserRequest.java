package com.bookrental.api.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EditUserRequest {

    @Schema(description = "New first name of the user", example = "John")
    private String firstName;

    @Schema(description = "New last name of the user", example = "Doe")
    private String lastName;

    @Schema(description = "Current password of the user", example = "oldPassword123")
    private String currentPassword;

    @Schema(description = "New password of the user", example = "newPassword123")
    private String newPassword;

}
