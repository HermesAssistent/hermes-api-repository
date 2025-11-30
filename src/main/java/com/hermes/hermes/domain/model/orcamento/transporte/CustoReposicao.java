package com.hermes.hermes.domain.model.orcamento.transporte;

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
import java.math.RoundingMode;

@Entity
@Table(name = "custo_reposicao")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustoReposicao extends ItemOrcamento {
    
    @Column(name = "valor_carga", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorCarga;
    
    @Column(name = "percentual_perda", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentualPerda;
    
    @Column(name = "tipo_carga", length = 100)
    private String tipoCarga;
    
    @Column(name = "valor_salvados", precision = 19, scale = 2)
    private BigDecimal valorSalvados;
    
    @Override
    public BigDecimal calcularSubtotal() {
        if (valorCarga == null || percentualPerda == null) {
            return BigDecimal.ZERO;
        }
        
        // Calcula valor da perda: valorCarga * (percentualPerda / 100)
        BigDecimal valorPerda = valorCarga.multiply(percentualPerda)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // Subtrai salvados se houver
        if (valorSalvados != null) {
            valorPerda = valorPerda.subtract(valorSalvados);
        }
        
        return valorPerda.max(BigDecimal.ZERO); // Não pode ser negativo
    }
    
    
    public BigDecimal calcularComDepreciacao(BigDecimal percentualDepreciacao) {
        if (percentualDepreciacao == null || percentualDepreciacao.compareTo(BigDecimal.ZERO) <= 0) {
            return calcularSubtotal();
        }
        
        BigDecimal fatorDepreciacao = BigDecimal.ONE.subtract(
            percentualDepreciacao.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );
        
        return calcularSubtotal().multiply(fatorDepreciacao);
    }
    
    
    public BigDecimal calcularValorLiquido() {
        BigDecimal valorBruto = calcularSubtotal();
        if (valorSalvados != null) {
            return valorBruto.subtract(valorSalvados).max(BigDecimal.ZERO);
        }
        return valorBruto;
    }
    
    
    public boolean isPerdaTotal() {
        return percentualPerda != null && 
               percentualPerda.compareTo(new BigDecimal("75")) >= 0;
    }
    
    
    public boolean isCargaEspecial() {
        return tipoCarga != null && 
               (tipoCarga.toLowerCase().contains("perecivel") ||
                tipoCarga.toLowerCase().contains("fragil") ||
                tipoCarga.toLowerCase().contains("controlada") ||
                tipoCarga.toLowerCase().contains("refrigerada"));
    }
    
    
    public BigDecimal calcularComAcrescimoCargaEspecial() {
        BigDecimal valorBase = calcularSubtotal();
        if (isCargaEspecial()) {
            return valorBase.multiply(new BigDecimal("1.15")); // +15%
        }
        return valorBase;
    }
    
    @Override
    public boolean isValido() {
        return super.isValido() &&
               valorCarga != null && valorCarga.compareTo(BigDecimal.ZERO) > 0 &&
               percentualPerda != null && percentualPerda.compareTo(BigDecimal.ZERO) >= 0 &&
               percentualPerda.compareTo(new BigDecimal("100")) <= 0;
    }
}