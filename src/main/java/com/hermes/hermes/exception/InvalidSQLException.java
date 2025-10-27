package com.hermes.hermes.exception;

// Exceção para SQL inválido gerado
public class InvalidSQLException extends RuntimeException {
    private final String generatedSQL;

    public InvalidSQLException(String message, String generatedSQL) {
        super(message + " | SQL gerado: " + generatedSQL);
        this.generatedSQL = generatedSQL;
    }

    public String getGeneratedSQL() {
        return generatedSQL;
    }
}