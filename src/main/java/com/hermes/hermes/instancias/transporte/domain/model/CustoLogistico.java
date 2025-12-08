package com.hermes.hermes.instancias.transporte.domain.model;

import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.instancias.transporte.domain.enums.TipoCustoLogistico;
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
@Table(name = "custo_logistico")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustoLogistico extends ItemOrcamento {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_custo", nullable = false)
    private TipoCustoLogistico tipo;
    
    @Column(name = "unidade_medida", length = 50)
    private String unidadeMedida; // dias, m³, tonelada, etc.
    
    @Column(name = "periodo_dias")
    private Integer periodoDias;
    
    @Column(name = "peso_volume_m3", precision = 10, scale = 2)
    private BigDecimal pesoVolumeM3;
    
    @Column(name = "taxa_diaria", precision = 19, scale = 2)
    private BigDecimal taxaDiaria;
    
    @Override
    public BigDecimal calcularSubtotal() {
        if (valor == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal subtotal = valor.multiply(new BigDecimal(quantidade));
        
        // Para armazenagem, considera período e taxa diária
        if (tipo == TipoCustoLogistico.ARMAZENAGEM && 
            taxaDiaria != null && periodoDias != null) {
            BigDecimal custoArmazenagem = taxaDiaria.multiply(new BigDecimal(periodoDias));
            subtotal = subtotal.add(custoArmazenagem);
        }
        
        return subtotal;
    }
    
    
    public BigDecimal calcularPorVolumeOuPeso() {
        BigDecimal subtotal = calcularSubtotal();
        
        if (pesoVolumeM3 != null && pesoVolumeM3.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal multiplicador = getMultiplicadorVolume();
            return subtotal.multiply(multiplicador);
        }
        
        return subtotal;
    }
    
    
    private BigDecimal getMultiplicadorVolume() {
        if (pesoVolumeM3 == null) {
            return BigDecimal.ONE;
        }
        
        // Faixas de volume com multiplicadores
        if (pesoVolumeM3.compareTo(new BigDecimal("100")) > 0) {
            return new BigDecimal("1.5");  // >100m³: +50%
        } else if (pesoVolumeM3.compareTo(new BigDecimal("50")) > 0) {
            return new BigDecimal("1.3");  // 50-100m³: +30%
        } else if (pesoVolumeM3.compareTo(new BigDecimal("10")) > 0) {
            return new BigDecimal("1.1");  // 10-50m³: +10%
        }
        
        return BigDecimal.ONE; // ≤10m³: sem acréscimo
    }
    
    
    public BigDecimal calcularComUrgencia(boolean isUrgente) {
        BigDecimal valorBase = calcularPorVolumeOuPeso();
        if (isUrgente) {
            return valorBase.multiply(new BigDecimal("1.4")); // +40%
        }
        return valorBase;
    }
    
    
    public boolean isOperacaoLongoPrazo() {
        return periodoDias != null && periodoDias > 30;
    }
    
    
    public BigDecimal calcularComDescontoLongoPrazo() {
        BigDecimal valorBase = calcularPorVolumeOuPeso();
        if (isOperacaoLongoPrazo()) {
            return valorBase.multiply(new BigDecimal("0.85")); // 15% de desconto
        }
        return valorBase;
    }
    
    
    public boolean requerManuseioEspecial() {
        return tipo == TipoCustoLogistico.MANUSEIO_ESPECIAL ||
               (descricao != null && 
                (descricao.toLowerCase().contains("fragil") ||
                 descricao.toLowerCase().contains("perigoso") ||
                 descricao.toLowerCase().contains("controlado")));
    }
    
    @Override
    public boolean isValido() {
        return super.isValido() &&
               tipo != null;
    }
}