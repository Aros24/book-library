package com.bookrental.api.rent.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Rent {

    @Schema(description = "Unique public ID of the rent in UUID form")
    String publicId;

    @Schema(description = "Public ID of the user in UUID form")
    String userPublicId;

    @Schema(description = "Public ID of the book in UUID form")
    String bookPublicId;

    @Schema(description = "Title of the book")
    String bookTitle;

    @Schema(description = "Authors of the book")
    List<String> bookAuthors;

    @Schema(description = "Start date of the rent")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    LocalDateTime startDate;

    @Schema(description = "End date of the rent")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    LocalDateTime endDate;

}