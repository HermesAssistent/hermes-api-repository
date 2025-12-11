package com.hermes.hermes.instancias.residencial.domain.model;

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
@Table(name = "material")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material extends ItemOrcamento {
    
    @Column(name = "unidade_medida", length = 20)
    private String unidadeMedida;
    
    @Column(name = "quantidade_estoque")
    private Integer quantidadeEstoque;
    
    @Column(length = 100)
    private String categoria;
    
    @Column(length = 50)
    private String fornecedor;
    
    @Override
    public BigDecimal calcularSubtotal() {
        if (valor == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return valor.multiply(new BigDecimal(quantidade));
    }
    
    
    public boolean temEstoqueSuficiente() {
        if (quantidadeEstoque == null || quantidade == null) {
            return false;
        }
        return quantidadeEstoque >= quantidade;
    }
    
    
    public BigDecimal calcularComDesperdicio(BigDecimal percentualDesperdicio) {
        if (percentualDesperdicio == null || percentualDesperdicio.compareTo(BigDecimal.ZERO) <= 0) {
            return calcularSubtotal();
        }
        
        BigDecimal multiplicador = BigDecimal.ONE.add(percentualDesperdicio.divide(new BigDecimal("100")));
        return calcularSubtotal().multiply(multiplicador);
    }
    
    
    public boolean isMaterialNobre() {
        return categoria != null && 
               (categoria.toLowerCase().contains("premium") ||
                categoria.toLowerCase().contains("importado") ||
                categoria.toLowerCase().contains("especial"));
    }
    
    @Override
    public boolean isValido() {
        return super.isValido() &&
               unidadeMedida != null && !unidadeMedida.trim().isEmpty();
    }
}