package com.hermes.hermes.domain.model.oficina;

import com.hermes.hermes.domain.model.abstracts.Entidade;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orcamento extends Entidade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orc_orcamento_seq")
    @SequenceGenerator(name = "orc_orcamento_seq", sequenceName = "orc_orcamento_seq", allocationSize = 1)
    private Long id;

    @Column(length = 1000)
    private String descricao;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorPecas;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorMaoDeObra;

    private LocalDate prazo;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Peca> pecas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinistro_id")
    private Sinistro sinistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oficina_id")
    private Oficina oficina;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void atualizarValorPecasAPartirDasPecas() {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        if (this.pecas != null) {
            for (Peca p : this.pecas) {
                if (p != null && p.getValor() != null) {
                    total = total.add(p.getValor());
                }
            }
        }
        this.valorPecas = total;
    }

    @Override
    public String toString() {
        return "Orcamento{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", valorPecas=" + valorPecas +
                ", valorMaoDeObra=" + valorMaoDeObra +
                ", prazo=" + prazo +
                ", pecas=" + pecas +
                ", sinistro=" + (sinistro != null ? sinistro.getId() : null) +
                ", oficina=" + (oficina != null ? oficina.getId() : null) +
                '}';
    }
}
