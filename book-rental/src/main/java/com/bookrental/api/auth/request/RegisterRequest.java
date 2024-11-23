package com.bookrental.api.auth.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegisterRequest {

    @NotNull
    @Schema(description = "First name of the user", example = "John")
    String firstName;

    @NotNull
    @Schema(description = "Last name of the user", example = "Doe")
    String lastName;

    @NotNull
    @Schema(description = "Email address of the user", example = "user@example.com")
    String email;

    @NotNull
    @Schema(description = "Password for the user account", example = "password123")
    String password;

}
