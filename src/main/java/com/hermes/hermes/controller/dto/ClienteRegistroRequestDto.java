package com.hermes.hermes.controller.dto;

import lombok.Data;

@Data
public class ClienteRegistroRequestDto {
    private String email;
    private String senha;
    private String nome;
    private String cpf;
    private String veiculo;
}
