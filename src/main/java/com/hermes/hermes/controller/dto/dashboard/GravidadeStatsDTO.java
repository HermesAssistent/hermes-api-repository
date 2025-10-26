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
    private Long leve;
    private Long moderada;
    private Long grave;
}