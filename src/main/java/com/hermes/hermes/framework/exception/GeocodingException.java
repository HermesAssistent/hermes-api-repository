package com.hermes.hermes.framework.exception;

public class GeocodingException extends RuntimeException {
    public GeocodingException(String message) {
        super(message);
    }

    public GeocodingException(String message, Throwable cause) {
        super(message, cause);
    }
}