package com.hermes.hermes.controller.dto;

import com.hermes.hermes.domain.model.seguradora.Seguradora;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeguradoraResponseDto {
    private Seguradora seguradora;
    private String token;
}
