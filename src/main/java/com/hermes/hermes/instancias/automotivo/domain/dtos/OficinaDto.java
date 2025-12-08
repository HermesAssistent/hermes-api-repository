package com.hermes.hermes.instancias.automotivo.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OficinaDto {

    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String endereco;

    @NotNull
    private List<String> especialidades;

    private Double latitude;

    private Double longitude;
}