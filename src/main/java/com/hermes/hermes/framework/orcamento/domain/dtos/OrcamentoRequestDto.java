package com.hermes.hermes.framework.orcamento.domain.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Builder.Default
    private Boolean gerarItensAutomaticamente = true;

    private String tipoSinistro;
}