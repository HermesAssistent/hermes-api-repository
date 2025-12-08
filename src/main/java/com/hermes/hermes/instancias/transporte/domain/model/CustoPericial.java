package com.hermes.hermes.instancias.transporte.domain.model;

import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.instancias.transporte.domain.enums.TipoAvaliacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "custo_pericial")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustoPericial extends ItemOrcamento {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_avaliacao", nullable = false)
    private TipoAvaliacao tipoAvaliacao;
    
    @Column(name = "perito_responsavel", length = 200)
    private String peritoResponsavel;
    
    @Column(name = "tempo_estimado_dias")
    private Integer tempoEstimadoDias;
    
    @Column(name = "valor_deslocamento", precision = 19, scale = 2)
    private BigDecimal valorDeslocamento;
    
    @Override
    public BigDecimal calcularSubtotal() {
        if (valor == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal subtotal = valor.multiply(new BigDecimal(quantidade));
        
        // Adiciona custo de deslocamento se informado
        if (valorDeslocamento != null) {
            subtotal = subtotal.add(valorDeslocamento);
        }
        
        return subtotal;
    }
    
    
    public BigDecimal calcularComTipoAvaliacao() {
        BigDecimal subtotal = calcularSubtotal();
        BigDecimal multiplicador = getMultiplicadorTipoAvaliacao();
        return subtotal.multiply(multiplicador);
    }
    
    
    private BigDecimal getMultiplicadorTipoAvaliacao() {
        if (tipoAvaliacao == null) {
            return BigDecimal.ONE;
        }
        
        return switch (tipoAvaliacao) {
            case EMERGENCIAL -> new BigDecimal("2.0");   // +100%
            case COMPLETA -> new BigDecimal("1.5");      // +50%
            case VISTORIA -> new BigDecimal("1.2");      // +20%
            case SIMPLES -> BigDecimal.ONE;              // sem acréscimo
        };
    }
    
    
    public BigDecimal calcularComUrgencia(boolean isUrgente) {
        BigDecimal valorBase = calcularComTipoAvaliacao();
        if (isUrgente) {
            return valorBase.multiply(new BigDecimal("1.3")); // +30%
        }
        return valorBase;
    }
    
    
    public boolean requerDeslocamento() {
        return valorDeslocamento != null && valorDeslocamento.compareTo(BigDecimal.ZERO) > 0;
    }
    
    
    public boolean isAvaliacaoComplexa() {
        return tipoAvaliacao == TipoAvaliacao.COMPLETA || 
               tipoAvaliacao == TipoAvaliacao.EMERGENCIAL;
    }
    
    @Override
    public boolean isValido() {
        return super.isValido() &&
               tipoAvaliacao != null;
    }
}