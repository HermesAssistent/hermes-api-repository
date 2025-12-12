package com.hermes.hermes.framework.sinistro.domain.model;

import com.hermes.hermes.framework.sinistro.domain.dtos.SinistroBaseDto;
import com.hermes.hermes.framework.abstracts.Entidade;
import com.hermes.hermes.framework.chat.domain.model.Foto;
import com.hermes.hermes.framework.cliente.domain.model.Cliente;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "sinistro")
public abstract class SinistroBase extends Entidade {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sin_sinistro_seq")
    @SequenceGenerator(name = "sin_sinistro_seq", sequenceName = "sin_sinistro_seq", allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    private String problema;
    private String data;
    private String hora;
    private String categoriaProblema;
    @Enumerated(EnumType.STRING)
    private TipoSinistro tipo;

    @OneToMany(mappedBy = "sinistro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos;

    public abstract SinistroBaseDto toDto();
}
