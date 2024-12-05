package com.bookrental.persistence.constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum RentConstants {

    PUBLIC_ID("userPublicId"),
    START_DATE ("startDate"),
    BOOK("book");

    String value;

    RentConstants(String name) {
        this.value = name;
    }

}
