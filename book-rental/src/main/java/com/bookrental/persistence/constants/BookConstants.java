package com.bookrental.persistence.constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BookConstants {

    TITLE("title"),
    PUBLISHER("publisher"),
    PUBLICATION_YEAR("publicationYear"),
    AUTHORS_JOIN("authors");

    String value;

    BookConstants(String name) {
        this.value = name;
    }

}
