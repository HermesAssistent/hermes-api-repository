package com.hermes.hermes.domain.model;

import jakarta.persistence.*;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private String nome;
    @Column(nullable=false, unique=true, length=11)
    private String cpf;
    private String veiculo;

    public Cliente() { }

    public Cliente(String nome, String cpf, String veiculo) {
        this.nome = nome;
        this.cpf = cpf;
        this.veiculo = veiculo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }
}
