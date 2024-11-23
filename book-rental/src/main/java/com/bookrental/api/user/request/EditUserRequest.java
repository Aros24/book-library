package com.bookrental.api.user.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EditUserRequest {

    @Schema(description = "New first name of the user", example = "John")
    String firstName;

    @Schema(description = "New last name of the user", example = "Doe")
    String lastName;

    @Schema(description = "Current password of the user", example = "oldPassword123")
    String currentPassword;

    @Schema(description = "New password of the user", example = "newPassword123")
    String newPassword;

}
