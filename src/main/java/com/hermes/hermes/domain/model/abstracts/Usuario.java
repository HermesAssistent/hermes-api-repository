package com.hermes.hermes.domain.model.abstracts;

import lombok.Data;
import java.io.Serializable;

@Data
public abstract class Usuario extends Entidade implements Serializable {
    private String nome;
    private String login;
    private String email;
    private String senha;
    private String endereco;
    private String telefone;
    private String celular;
}
