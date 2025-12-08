package com.hermes.hermes.instancias.transporte.domain.enums;

/**
 * Tipos de carga transportada.
 */
public enum TipoCarga {
    /**
     * Carga que pode deteriorar (alimentos, medicamentos)
     */
    PERECIVEL,
    
    /**
     * Carga perigosa (químicos, inflamáveis)
     */
    PERIGOSA,
    
    /**
     * Carga frágil (vidros, eletrônicos)
     */
    FRAGIL,
    
    /**
     * Carga geral sem características especiais
     */
    GERAL
}