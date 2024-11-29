package com.bookrental.persistence.constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum AuthorConstants {

    NAME("name");

    String value;

    AuthorConstants(String name) {
        this.value = name;
    }

}
