package com.bookrental.service.book;

import com.bookrental.service.author.AuthorDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookDto {

    String publicId;
    String title;
    String isbn;
    int amount;
    int publicationYear;
    String publisher;
    List<AuthorDto> authors;

}
