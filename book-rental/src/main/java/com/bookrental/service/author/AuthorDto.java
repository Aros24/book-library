package com.bookrental.service.author;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorDto {

    String publicId;
    String name;
    int birthYear;

}
