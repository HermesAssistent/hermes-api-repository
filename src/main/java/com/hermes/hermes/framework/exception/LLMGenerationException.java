package com.hermes.hermes.framework.exception;

// Exceção para erros na geração de SQL pelo LLM
public class LLMGenerationException extends RuntimeException {
    public LLMGenerationException(String message) {
        super(message);
    }

    public LLMGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}