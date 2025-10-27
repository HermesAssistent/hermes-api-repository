package com.hermes.hermes.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class OficinaResponseDto {
    private Long id;
    private String nome;
    private String endereco;
    private List<String> especialidades;
    private Double latitude;
    private Double longitude;
    private String cep;
    private String telefone;
}
