package com.bookrental.api.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthResponse {

    @JsonProperty("user_public_id")
    String userPublicId;

}
