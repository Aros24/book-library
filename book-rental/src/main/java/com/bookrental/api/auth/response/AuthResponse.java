package com.bookrental.api.auth.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthResponse {

    @Schema(description = "The public ID of the authenticated user in uuid form")
    String userPublicId;

    @Schema(description = "Users' role for front-end purposes")
    String role;

}
