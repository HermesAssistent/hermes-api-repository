package com.hermes.hermes.domain.model.abstracts;

import jakarta.persistence.Column;
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
    private String endereco;
    private String telefone;
    private String celular;

    public abstract String getRole();
}
