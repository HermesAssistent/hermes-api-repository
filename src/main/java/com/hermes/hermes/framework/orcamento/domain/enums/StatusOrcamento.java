package com.hermes.hermes.framework.orcamento.domain.enums;

/**
 * Status dos orçamentos no sistema.
 * Define os estados possíveis de um orçamento.
 */
public enum StatusOrcamento {
    /**
     * Orçamento criado, aguardando análise
     */
    PENDENTE,
    
    /**
     * Orçamento aceito pelo cliente
     */
    ACEITO,
    
    /**
     * Orçamento em revisão com observações
     */
    REVISADO,
    
    /**
     * Orçamento rejeitado pelo cliente
     */
    REJEITADO
}