package com.bookrental.service.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserDto {

    private String publicId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private boolean deleted;

}
