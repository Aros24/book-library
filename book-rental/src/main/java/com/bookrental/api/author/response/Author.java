package com.bookrental.api.author.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Author {

    @Schema(description = "Public ID of the author", example = "123e4567-e89b-12d3-a456-426614174000")
    String publicId;

    @Schema(description = "Name of the author", example = "Jane Austen")
    String name;

    @Schema(description = "Birth year of the author", example = "1775")
    int birthYear;

}
