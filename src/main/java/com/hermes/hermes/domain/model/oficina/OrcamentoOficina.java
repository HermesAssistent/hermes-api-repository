package com.hermes.hermes.domain.model.oficina;

import com.hermes.hermes.framework.abstracts.Entidade;
import com.hermes.hermes.instancias.automotivo.domain.model.Oficina;
import com.hermes.hermes.instancias.automotivo.domain.model.SinistroAutomotivo;
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
public class OrcamentoOficina extends Entidade {

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
    private List<PecaOficina> pecas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinistro_id")
    private SinistroAutomotivo sinistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oficina_id")
    private Oficina oficina;

    @Enumerated(EnumType.STRING)
    private com.hermes.hermes.domain.model.oficina.OrcamentoStatus status = com.hermes.hermes.domain.model.oficina.OrcamentoStatus.PENDING;

    @Column(length = 1000)
    private String reviewNotes;

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
            for (PecaOficina p : this.pecas) {
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
