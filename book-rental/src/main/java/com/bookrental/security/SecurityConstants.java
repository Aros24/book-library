package com.bookrental.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum SecurityConstants {

    BEARER("Bearer "),
    AUTHORIZATION("Authorization");

    String value;

    SecurityConstants(String value) {
        this.value = value;
    }

}
