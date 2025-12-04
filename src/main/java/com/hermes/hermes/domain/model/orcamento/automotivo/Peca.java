package com.hermes.hermes.domain.model.orcamento.automotivo;

import com.hermes.hermes.domain.model.orcamento.ItemOrcamento;
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
@Table(name = "peca")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Peca extends ItemOrcamento {
    
    @Column(length = 100, unique = true)
    private String codigo;
    
    @Column(length = 100)
    private String categoria;
    
    @Column(length = 50)
    private String marca;
    
    @Override
    public BigDecimal calcularSubtotal() {
        if (valor == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return valor.multiply(new BigDecimal(quantidade));
    }
    
    
    public boolean isOriginal() {
        return marca != null && !marca.toLowerCase().contains("paralela");
    }
    
    
    public BigDecimal aplicarDesconto(BigDecimal percentualDesconto) {
        if (percentualDesconto == null || percentualDesconto.compareTo(BigDecimal.ZERO) <= 0) {
            return calcularSubtotal();
        }
        
        BigDecimal desconto = percentualDesconto.divide(new BigDecimal("100"));
        BigDecimal valorDesconto = calcularSubtotal().multiply(desconto);
        return calcularSubtotal().subtract(valorDesconto);
    }
    
    @Override
    public boolean isValido() {
        return super.isValido() && 
               codigo != null && !codigo.trim().isEmpty();
    }
}