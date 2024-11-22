package com.bookrental.api.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthResponse {

    @JsonProperty("user_public_id")
    @Schema(description = "The public ID of the authenticated user in uuid form")
    String userPublicId;

    @JsonProperty("role")
    @Schema(description = "Users' role for front-end purposes")
    String role;

}
