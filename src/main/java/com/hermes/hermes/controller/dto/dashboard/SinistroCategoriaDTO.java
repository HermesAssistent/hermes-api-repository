package com.hermes.hermes.controller.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SinistroCategoriaDTO {
    @JsonProperty("categoria")
    private String categoria;

    @JsonProperty("qtd")
    private Integer qtd;
}