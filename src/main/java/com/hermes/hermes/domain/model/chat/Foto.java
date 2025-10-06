package com.hermes.hermes.domain.model.chat;

import com.hermes.hermes.domain.model.abstracts.Entidade;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
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
    private ChatSession chatSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinistro_id")
    private Sinistro sinistro;
}
