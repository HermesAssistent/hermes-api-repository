package com.hermes.hermes.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoResponseDto {
    private Long id;
    private String descricao;
    private BigDecimal valorPecas;
    private BigDecimal valorMaoDeObra;
    private LocalDate prazo;
    private Long sinistroId;
    private Long oficinaId;
    private List<PecaResponseDto> pecas;
    private String status;
    private String reviewNotes;
}
