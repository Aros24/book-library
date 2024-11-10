package com.bookrental.config.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ErrorResponse {

    @Schema(description = "The error code associated with the issue (e.g., 400, 409, etc.)")
    int statusCode;

    @Schema(description = "The error message describing what went wrong")
    String message;

    @Schema(description = "The timestamp of when the error occurred")
    LocalDateTime timestamp;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
    }

}
