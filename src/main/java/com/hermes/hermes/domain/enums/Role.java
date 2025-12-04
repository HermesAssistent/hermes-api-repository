package com.hermes.hermes.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    CLIENTE("CLIENTE"),
    SEGURADORA("SEGURADORA");

    private final String valor;

    Role(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }
}
