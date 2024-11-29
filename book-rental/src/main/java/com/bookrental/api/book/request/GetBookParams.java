package com.bookrental.api.book.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetBookParams {

    @Schema(description = "Title of the book to filter by", example = "The Great Gatsby")
    private String title;

    @Schema(description = "Publisher of the book to filter by", example = "Scribner")
    private String publisher;

    @Schema(description = "Publication year of the book to filter by", example = "1925")
    private Integer publicationYear;

    @Schema(description = "Author's name to filter books by", example = "F. Scott Fitzgerald")
    private String authorName;

    @Schema(description = "Number of records per page", example = "10", minimum = "1")
    private Integer size;

    @Schema(description = "Page number to retrieve", example = "0", minimum = "0")
    private Integer page;

    @Schema(description = "Field by which to order the results", example = "title")
    private String orderBy;

    @Schema(description = "Direction of the order, either ASC (ascending) or DESC (descending)", example = "ASC", allowableValues = {"ASC", "DESC"})
    private String orderDirection;

}
