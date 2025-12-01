package com.hermes.hermes.domain.model.prestador;

import com.hermes.hermes.domain.enums.TipoPrestador;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Representa um prestador de serviços domésticos.
 * Especializado em serviços residenciais como elétrica, hidráulica, etc.
 */
@Entity
@Table(name = "prestador_servico")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PrestadorServico extends Prestador {
    
    @Enumerated(EnumType.STRING)
    private TipoPrestador tipoPrestador;
    
    /**
     * Avalia o prestador de serviço baseado em sua especialização.
     */
    @Override
    public double avaliar() {
        // TODO: Implementar lógica de avaliação específica para serviços domésticos
        return 4.2; // Valor padrão
    }
    
    /**
     * Verifica disponibilidade do prestador de serviço.
     */
    @Override
    public boolean verificarDisponibilidade() {
        // TODO: Implementar verificação de agenda do prestador
        return true; // Valor padrão
    }
}