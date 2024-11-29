package com.bookrental.api.book.response;

import com.bookrental.api.author.response.Author;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Book {

    @Schema(description = "Public id of the book in the UUID form")
    String publicId;

    @Schema(description = "Title of the book", example = "The Great Gatsby")
    String title;

    @Schema(description = "ISBN of the book", example = "9780743273565")
    String isbn;

    @Schema(description = "Amount of the book in the stock", example = "5")
    int amount;

    @Schema(description = "Publication year of the book", example = "1925")
    int publicationYear;

    @Schema(description = "Publisher of the book", example = "Scribner")
    String publisher;

    @Schema(description = "List of authors associated with the book")
    List<Author> authors;

}
