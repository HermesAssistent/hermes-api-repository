package com.hermes.hermes.domain.model.oficina;

import com.hermes.hermes.domain.model.abstracts.Entidade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String endereco;
}
