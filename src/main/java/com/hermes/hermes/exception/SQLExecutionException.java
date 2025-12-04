package com.hermes.hermes.exception;

// Exceção para erros na execução do SQL
public class SQLExecutionException extends RuntimeException {
    private final String executedSQL;

    public SQLExecutionException(String message, String executedSQL, Throwable cause) {
        super(message + " | SQL executado: " + executedSQL, cause);
        this.executedSQL = executedSQL;
    }

    public String getExecutedSQL() {
        return executedSQL;
    }
}