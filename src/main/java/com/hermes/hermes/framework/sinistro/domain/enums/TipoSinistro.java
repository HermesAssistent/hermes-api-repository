package com.hermes.hermes.framework.sinistro.domain.enums;

import lombok.Getter;

@Getter
public enum TipoSinistro {
    AUTOMOTIVO("automotivo"),
    RESIDENCIAL("residencial", "domestico"),
    TRANSPORTE("transporte", "carga");

    private final String[] aliases;

    TipoSinistro(String... aliases) {
        this.aliases = aliases;
    }

    public String getPrimaryAlias() {
        return aliases[0];
    }

    /**
     * Busca o tipo de sinistro a partir de uma string (case-insensitive)
     */
    public static TipoSinistro fromString(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo de sinistro não pode ser nulo ou vazio");
        }

        String tipoLower = tipo.toLowerCase().trim();

        for (TipoSinistro ts : values()) {
            for (String alias : ts.aliases) {
                if (alias.equalsIgnoreCase(tipoLower)) {
                    return ts;
                }
            }
        }

        throw new IllegalArgumentException(
                "Tipo de sinistro inválido: " + tipo +
                        ". Tipos válidos: AUTOMOTIVO, RESIDENCIAL, CARGA"
        );
    }
}
