package com.bookrental.config.exceptions;

import java.nio.file.AccessDeniedException;

public class ForbiddenException extends AccessDeniedException {

    public ForbiddenException(String message) {
        super(message);
    }

}
