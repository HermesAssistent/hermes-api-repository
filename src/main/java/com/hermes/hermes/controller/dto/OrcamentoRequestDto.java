package com.hermes.hermes.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
public class OrcamentoRequestDto {

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @NotNull(message = "Valor da mão de obra é obrigatório")
    @PositiveOrZero(message = "Valor da mão de obra deve ser positivo ou zero")
    private BigDecimal valorMaoDeObra;

    private LocalDate prazo;

    @NotNull(message = "ID do sinistro é obrigatório")
    private Long sinistroId;

    @NotNull(message = "ID da oficina é obrigatório")
    private Long oficinaId;

    @Valid
    private List<PecaRequestDto> pecas;
}