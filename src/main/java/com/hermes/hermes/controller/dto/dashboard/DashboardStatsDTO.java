package com.hermes.hermes.controller.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Integer totalClientes;
    private Integer totalSinistros;
    private Integer totalOficinas;
    private Integer sinistrosComFeridos;
    private Integer sinistrosAtivos;
    private GravidadeStatsDTO gravidade;
}