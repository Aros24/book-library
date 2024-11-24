package com.bookrental.api.author.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetAuthorParams {

    @Schema(description = "Name to filter authors by (optional)", example = "Jane")
    String name;

    @Schema(description = "Page number for pagination - first page is 0", example = "0")
    Integer page;

    @Schema(description = "Size of the page for pagination", example = "10")
    Integer size;

}
