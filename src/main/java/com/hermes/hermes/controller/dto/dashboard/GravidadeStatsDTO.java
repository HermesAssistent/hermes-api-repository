package com.hermes.hermes.controller.dto.dashboard;

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