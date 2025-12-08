package com.hermes.hermes.instancias.domestico.domain.model;

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
@Table(name = "servico_tecnico")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoTecnico extends ItemOrcamento {
    
    @Column(name = "horas_estimadas", nullable = false)
    private Integer horasEstimadas;
    
    @Column(name = "valor_hora", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorHora;
    
    @Column(length = 100)
    private String especialidade;
    
    @Column(name = "complexidade_servico")
    private String complexidadeServico; // SIMPLES, MEDIO, COMPLEXO
    
    @Column(name = "requer_certificacao")
    private Boolean requerCertificacao = false;
    
    @Override
    public BigDecimal calcularSubtotal() {
        if (valorHora == null || horasEstimadas == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal custoBase = valorHora.multiply(new BigDecimal(horasEstimadas));
        return custoBase.multiply(new BigDecimal(quantidade));
    }
    
    
    public BigDecimal calcularComComplexidade() {
        BigDecimal subtotal = calcularSubtotal();
        BigDecimal multiplicador = getMultiplicadorComplexidade();
        return subtotal.multiply(multiplicador);
    }
    
    
    private BigDecimal getMultiplicadorComplexidade() {
        if (complexidadeServico == null) {
            return BigDecimal.ONE;
        }
        
        return switch (complexidadeServico.toUpperCase()) {
            case "COMPLEXO" -> new BigDecimal("1.8");
            case "MEDIO" -> new BigDecimal("1.4");
            case "SIMPLES" -> BigDecimal.ONE;
            default -> BigDecimal.ONE;
        };
    }
    
    
    public BigDecimal calcularComCertificacao() {
        BigDecimal subtotal = calcularComComplexidade();
        if (Boolean.TRUE.equals(requerCertificacao)) {
            return subtotal.multiply(new BigDecimal("1.25")); // +25%
        }
        return subtotal;
    }
    
    
    public boolean isEmergencial() {
        return especialidade != null && 
               (especialidade.toLowerCase().contains("emergencia") ||
                especialidade.toLowerCase().contains("urgente") ||
                especialidade.toLowerCase().contains("24h"));
    }
    
    
    public boolean requerEspecializacao() {
        return especialidade != null &&
               (especialidade.toLowerCase().contains("eletricista") ||
                especialidade.toLowerCase().contains("encanador") ||
                especialidade.toLowerCase().contains("gas"));
    }
    
    @Override
    public boolean isValido() {
        return super.isValido() &&
               horasEstimadas != null && horasEstimadas > 0 &&
               valorHora != null && valorHora.compareTo(BigDecimal.ZERO) > 0;
    }
}