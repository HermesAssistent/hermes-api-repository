package com.hermes.hermes.instancias.automotivo.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OficinaRequestDto {

    @NotBlank
    private String nome;

    private String cep;

    private String endereco;

    @NotNull
    private List<String> especialidades;

    private Double latitude;

    private Double longitude;
    private String telefone;
    private java.util.List<Long> seguradoraIds;
}
