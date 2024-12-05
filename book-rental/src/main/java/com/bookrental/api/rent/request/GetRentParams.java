package com.bookrental.api.rent.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetRentParams {

    @Schema(description = "Page number for pagination - first page is 0", example = "0")
    Integer page;

    @Schema(description = "Size of the page for pagination", example = "10")
    Integer size;

    @Schema(description = "Field to order by (optional)", example = "startDate")
    String orderBy;

    @Schema(description = "Direction of the order, either ASC (ascending) or DESC (descending)", example = "ASC", allowableValues = {"ASC", "DESC"})
    private String orderDirection;

}