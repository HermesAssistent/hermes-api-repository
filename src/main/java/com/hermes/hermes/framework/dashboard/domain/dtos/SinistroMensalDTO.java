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
public class SinistroMensalDTO {
    @JsonProperty("mes")
    private String mes;

    @JsonProperty("sinistros")
    private Integer sinistros;
}
