package com.hermes.hermes.domain.model.sinistro;

import com.hermes.hermes.domain.enums.StatusSinistro;
import com.hermes.hermes.domain.model.abstracts.Entidade;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.domain.model.oficina.Oficina;
import com.hermes.hermes.domain.model.seguradora.Seguradora;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sinistro extends Entidade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sin_sinistro_seq")
    @SequenceGenerator(name = "sin_sinistro_seq", sequenceName = "sin_sinistro_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguradora_id")
    private Seguradora seguradora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oficina_id")
    private Oficina oficina;

    private String relato;

    private String dados;

    private StatusSinistro status;
}
