package com.hermes.hermes.framework.dashboard.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SinistroRecenteDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("cliente")
    private String cliente;

    @JsonProperty("veiculo")
    private String veiculo;

    @JsonProperty("data")
    private String data;

    @JsonProperty("gravidade")
    private String gravidade;

    @JsonProperty("status")
    private String status;
}
