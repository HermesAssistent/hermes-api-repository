package com.hermes.hermes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationException extends ResponseStatusException {
    public AuthenticationException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
