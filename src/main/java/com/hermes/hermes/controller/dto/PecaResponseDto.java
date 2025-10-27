package com.hermes.hermes.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PecaResponseDto {
    private Long id;
    private String nome;
    private BigDecimal valor;
}
