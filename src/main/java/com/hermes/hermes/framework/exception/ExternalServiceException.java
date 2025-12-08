package com.hermes.hermes.framework.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String serviceName, String message) {
        super("Erro ao comunicar com " + serviceName + ": " + message);
    }
}
