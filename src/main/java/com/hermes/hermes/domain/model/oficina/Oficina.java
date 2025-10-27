package com.hermes.hermes.domain.model.oficina;

import com.hermes.hermes.domain.model.abstracts.Entidade;
import com.hermes.hermes.domain.model.localizacao.Localizacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.hermes.hermes.domain.model.seguradora.Seguradora;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Oficina extends Entidade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seg_oficina_seq")
    @SequenceGenerator(name = "seg_oficina_seq", sequenceName = "seg_oficina_seq", allocationSize = 1)

    private Long id;
    private String nome;
    private String telefone;

    @Embedded
    private Localizacao localizacao;

    @ElementCollection(fetch = FetchType.EAGER)
    private java.util.List<String> especialidades;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "oficina_seguradora",
            joinColumns = @JoinColumn(name = "oficina_id"),
            inverseJoinColumns = @JoinColumn(name = "seguradora_id"))
    private Set<Seguradora> seguradoras = new HashSet<>();
}
