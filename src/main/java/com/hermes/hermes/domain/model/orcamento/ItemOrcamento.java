package com.hermes.hermes.domain.model.orcamento;

import com.hermes.hermes.domain.model.abstracts.Entidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public abstract class ItemOrcamento extends Entidade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_orcamento_seq")
    @SequenceGenerator(name = "item_orcamento_seq", sequenceName = "item_orcamento_seq", allocationSize = 1)
    protected Long id;
    
    @Column(length = 500, nullable = false)
    protected String descricao;
    
    @Column(precision = 19, scale = 2, nullable = false)
    protected BigDecimal valor;
    
    @Column(nullable = false)
    protected Integer quantidade = 1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false)
    @JsonIgnore
    protected Orcamento orcamento;
    
    @Override
    public Long getId() {
        return this.id;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    public abstract BigDecimal calcularSubtotal();
    
    public boolean isValido() {
        return descricao != null && !descricao.trim().isEmpty() &&
               valor != null && valor.compareTo(BigDecimal.ZERO) >= 0 &&
               quantidade != null && quantidade > 0;
    }
}