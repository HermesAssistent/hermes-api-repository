package com.hermes.hermes.framework.orcamento.domain.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoRequestDto {

    @NotNull(message = "ID do sinistro é obrigatório")
    private Long sinistroId;

    private Long prestadorId;

    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    private String observacoes;

    @NotNull(message = "Tipo de sinistro é obrigatório")
    private String tipoSinistro;
    
    private List<ItemOrcamentoRequestDto> itens;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemOrcamentoRequestDto {
        @NotNull(message = "Descrição é obrigatória")
        private String descricao;
        
        @NotNull(message = "Valor é obrigatório")
        private BigDecimal valor;
        
        @NotNull(message = "Quantidade é obrigatória")
        private Integer quantidade;
        
        @NotNull(message = "Tipo do item é obrigatório")
        private String tipo;
        
        
        private String codigo;
        private String categoria;
        private String marca;
        private Integer horasEstimadas;
        private BigDecimal valorHora;
        private String especialidade;
        private String unidadeMedida;
        private String fornecedor;
        private BigDecimal percentualPerda;
        private BigDecimal valorCarga;
        
        private String tipoAvaliacao;
        
        private String tipoCustoLogistico;
    }
}