package com.hermes.hermes.framework.abstracts;

import com.hermes.hermes.framework.localizacao.domain.model.Localizacao;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class Usuario extends Entidade implements Serializable {
    private String uid; // ID do firebase
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false, unique = true)
    private String email;
    private String telefone;
    private String celular;
    @Embedded
    private Localizacao localizacao;

    public abstract String getRole();
}
