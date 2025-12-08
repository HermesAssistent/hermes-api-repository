package com.hermes.hermes.framework.chat.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hermes.hermes.framework.abstracts.Entidade;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "foto")
public class Foto extends Entidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;

    private String caminhoArquivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_session_id")
    @JsonIgnore
    private ChatSession chatSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinistro_id")
    @JsonIgnore
    private SinistroBase sinistro;
}
