package com.bookrental.api.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    String email;

    @NotNull
    @JsonProperty("password")
    String password;

}
