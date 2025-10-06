package com.hermes.hermes.domain.model.usuario;

import com.hermes.hermes.domain.model.abstracts.Entidade;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "usuario")
public class Usuario extends Entidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_seq", allocationSize = 1)
    private Long id;

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

    @Column(nullable = false)
    private String role;
}
