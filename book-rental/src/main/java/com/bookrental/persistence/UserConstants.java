package com.bookrental.persistence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum UserConstants {

    ID("id"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    ROLE("role"),
    DELETED("deleted");


    String value;

    UserConstants(String name) {
        this.value = name;
    }

}
