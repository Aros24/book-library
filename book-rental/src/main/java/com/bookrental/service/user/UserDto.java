package com.bookrental.service.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    String publicId;
    String firstName;
    String lastName;
    String email;
    String password;
    String role;
    boolean deleted;

}
