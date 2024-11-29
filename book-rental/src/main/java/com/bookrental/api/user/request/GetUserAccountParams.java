package com.bookrental.api.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetUserAccountParams {

    @Schema(description = "First name of the user", example = "John")
    String firstName;

    @Schema(description = "Last name of the user", example = "Doe")
    String lastName;

    @Schema(description = "Email address of the user", example = "user@example.com")
    String email;

    @Schema(description = "Role of the user", example = "basic")
    String role;

    @Schema(description = "Indicates if the user is deleted", example = "false")
    Boolean deleted;

    @Schema(description = "Page number for pagination - first page is 0", example = "0", minimum = "0")
    Integer page;

    @Schema(description = "Size of the page for pagination", example = "10", minimum = "1")
    Integer size;

    @Schema(description = "Field to order the results by", example = "firstName")
    String orderBy;

    @Schema(description = "Direction of sorting: 'asc' or 'desc'", example = "asc")
    String orderDirection;

}