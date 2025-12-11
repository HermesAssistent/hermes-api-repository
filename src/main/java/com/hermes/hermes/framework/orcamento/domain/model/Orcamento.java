package com.hermes.hermes.framework.orcamento.domain.model;

import com.hermes.hermes.framework.abstracts.Entidade;
import com.hermes.hermes.framework.prestador.domain.model.Prestador;
import com.hermes.hermes.framework.orcamento.domain.enums.StatusOrcamento;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orcamentos")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orcamento extends Entidade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orcamento_seq")
    @SequenceGenerator(name = "orcamento_seq", sequenceName = "orcamento_seq", allocationSize = 1)
    private Long id;
    
    @Column(precision = 19, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemOrcamento> itens = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusOrcamento status = StatusOrcamento.PENDENTE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id")
    private Prestador prestador;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinistro_id", nullable = false)
    private SinistroBase sinistro;
    
    @Column(length = 1000)
    private String observacoes;
    
    @Column(name = "data_criacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Override
    public Long getId() {
        return this.id;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    public void adicionarItem(ItemOrcamento item) {
        if (this.itens == null) {
            this.itens = new ArrayList<>();
        }
        item.setOrcamento(this);
        this.itens.add(item);
        calcularTotal();
    }
    
    public void removerItem(Long itemId) {
        if (this.itens != null) {
            this.itens.removeIf(item -> item.getId().equals(itemId));
            calcularTotal();
        }
    }
    
    public BigDecimal calcularTotal() {
        if (itens == null || itens.isEmpty()) {
            this.valorTotal = BigDecimal.ZERO;
            return this.valorTotal;
        }
        
        this.valorTotal = itens.stream()
                .filter(item -> item != null && item.isValido())
                .map(ItemOrcamento::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return this.valorTotal;
    }
    
    public void aceitar() {
        this.status = StatusOrcamento.ACEITO;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public void revisar(String observacoes) {
        this.status = StatusOrcamento.REVISADO;
        this.observacoes = observacoes;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}