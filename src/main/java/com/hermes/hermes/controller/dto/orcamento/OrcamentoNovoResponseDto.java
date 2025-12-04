package com.hermes.hermes.controller.dto.orcamento;

import com.hermes.hermes.domain.enums.StatusOrcamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoNovoResponseDto {

    private Long id;
    private BigDecimal valorTotal;
    private StatusOrcamento status;
    private Long sinistroId;
    private Long prestadorId;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private List<ItemOrcamentoResponseDto> itens;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemOrcamentoResponseDto {
        private Long id;
        private String descricao;
        private BigDecimal valor;
        private Integer quantidade;
        private BigDecimal subtotal;
        private String tipo; // PECA, MAO_DE_OBRA, MATERIAL, etc.
    }
}