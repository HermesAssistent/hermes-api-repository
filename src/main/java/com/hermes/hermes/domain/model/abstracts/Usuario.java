package com.hermes.hermes.domain.model.abstracts;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class Usuario extends Entidade implements Serializable {
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senha;
    private String endereco;
    private String telefone;
    private String celular;
}
