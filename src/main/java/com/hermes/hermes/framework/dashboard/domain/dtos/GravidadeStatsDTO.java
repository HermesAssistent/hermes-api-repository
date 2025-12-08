package com.hermes.hermes.framework.dashboard.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GravidadeStatsDTO {
    private Long baixa;
    private Long moderada;
    private Long alta;
}