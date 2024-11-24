package com.bookrental.api.book.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateBookRequest {

    @Schema(description = "Title of the book", example = "The Great Gatsby")
    String title;

    @Schema(description = "ISBN of the book", example = "9780743273565")
    String isbn;

    @Schema(description = "Publication year of the book", example = "1925")
    int publicationYear;

    @Schema(description = "Publisher of the book", example = "Scribner")
    String publisher;

    @Schema(description = "List of public IDs of authors associated with the book",
            example = "[\"author-public-id-1\", \"author-public-id-2\"]")
    List<String> authorPublicIds;

}

