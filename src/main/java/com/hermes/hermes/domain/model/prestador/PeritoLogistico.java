package com.hermes.hermes.domain.model.prestador;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Representa um perito logístico especializado em sinistros de transporte.
 * Responsável por avaliar cargas e realizar inspeções periciais.
 */
@Entity
@Table(name = "perito_logistico")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PeritoLogistico extends Prestador {
    
    @Column(name = "anos_experiencia")
    private Integer anosExperiencia;
    
    /**
     * Avalia o perito baseado em sua experiência e certificações.
     */
    @Override
    public double avaliar() {
        if (anosExperiencia == null) {
            return 3.0; // Valor padrão para peritos sem experiência informada
        }
        
        // Cálculo baseado na experiência (máximo 5.0)
        double avaliacao = Math.min(3.0 + (anosExperiencia * 0.1), 5.0);
        return avaliacao;
    }
    
    /**
     * Verifica disponibilidade do perito logístico.
     */
    @Override
    public boolean verificarDisponibilidade() {
        // TODO: Implementar verificação de agenda do perito
        return true; // Valor padrão
    }
    
    /**
     * Realiza inspeção pericial de carga.
     * @param carga informações sobre a carga
     * @return relatório da inspeção
     */
    public String realizarInspecao(Object carga) {
        // TODO: Implementar lógica de inspeção
        return "Relatório de inspeção gerado pelo perito " + this.nome;
    }
}