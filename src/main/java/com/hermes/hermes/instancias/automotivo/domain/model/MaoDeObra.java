package com.hermes.hermes.instancias.automotivo.domain.model;

import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "mao_de_obra")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaoDeObra extends ItemOrcamento {
    
    @Column(name = "horas_estimadas", nullable = false)
    private Integer horasEstimadas;
    
    @Column(name = "valor_hora", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorHora;
    
    @Column(length = 100)
    private String especialidade;
    
    @Column(name = "dificuldade_servico")
    private String dificuldadeServico; // BAIXA, MEDIA, ALTA
    
    @Override
    public BigDecimal calcularSubtotal() {
        if (valorHora == null || horasEstimadas == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal custoBase = valorHora.multiply(new BigDecimal(horasEstimadas));
        return custoBase.multiply(new BigDecimal(quantidade));
    }
    
    
    public BigDecimal calcularComDificuldade() {
        BigDecimal subtotal = calcularSubtotal();
        BigDecimal multiplicador = getMultiplicadorDificuldade();
        return subtotal.multiply(multiplicador);
    }
    
    
    private BigDecimal getMultiplicadorDificuldade() {
        if (dificuldadeServico == null) {
            return BigDecimal.ONE;
        }
        
        return switch (dificuldadeServico.toUpperCase()) {
            case "ALTA" -> new BigDecimal("1.5");
            case "MEDIA" -> new BigDecimal("1.2");
            case "BAIXA" -> BigDecimal.ONE;
            default -> BigDecimal.ONE;
        };
    }
    
    
    public boolean isEspecializado() {
        return especialidade != null && 
               (especialidade.toLowerCase().contains("eletrica") ||
                especialidade.toLowerCase().contains("injecao") ||
                especialidade.toLowerCase().contains("cambio"));
    }
    
    @Override
    public boolean isValido() {
        return super.isValido() &&
               horasEstimadas != null && horasEstimadas > 0 &&
               valorHora != null && valorHora.compareTo(BigDecimal.ZERO) > 0;
    }
}