package com.bookrental.api.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegisterRequest {

    @NotNull
    @JsonProperty("first_name")
    @Schema(description = "First name of the user", example = "John")
    String firstName;

    @NotNull
    @JsonProperty("last_name")
    @Schema(description = "Last name of the user", example = "Doe")
    String lastName;

    @NotNull
    @JsonProperty("email")
    @Schema(description = "Email address of the user", example = "user@example.com")
    String email;

    @NotNull
    @JsonProperty("password")
    @Schema(description = "Password for the user account", example = "password123")
    String password;

}
