package com.hermes.hermes.domain.enums;

/**
 * Tipos de sinistros suportados pelo sistema.
 */
public enum TipoSinistro {
    // Automotivos
    COLISAO,
    PANE_MECANICA,
    ROUBO_FURTO,
    
    // Domésticos  
    VAZAMENTO_HIDRAULICO,
    PROBLEMA_ELETRICO,
    INFILTRACAO,
    INCENDIO_DOMESTICO,
    
    // Transporte
    AVARIA_CARGA,
    ROUBO_EXTRAVIO,
    ACIDENTE_TRANSPORTE,
    ATRASO_ENTREGA
}