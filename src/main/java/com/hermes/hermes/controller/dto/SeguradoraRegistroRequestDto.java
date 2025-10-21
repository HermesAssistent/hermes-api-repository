package com.hermes.hermes.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SeguradoraRegistroRequestDto {
    @NotEmpty(message = "Campo obrigatório")
    private String email;
    @NotEmpty(message = "Campo obrigatório")
    private String senha;
    @NotEmpty(message = "Campo obrigatório")
    private String nome;
    private String endereco;
    private String telefone;
    private String celular;
    @NotEmpty(message = "Campo obrigatório")
    private String cnpj;
}
