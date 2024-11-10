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
public class LoginRequest {

    @NotNull
    @JsonProperty("email")
    @Schema(description = "Email address of the user", example = "user@example.com")
    String email;

    @NotNull
    @JsonProperty("password")
    @Schema(description = "Password of the user", example = "password123")
    String password;

}
