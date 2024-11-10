package com.bookrental.api.user.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetUserResponse {

    String publicId;
    String firstName;
    String lastName;
    String email;
    String role;

}
