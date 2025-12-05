package com.hermes.hermes.framework.seguradora.domain.dtos;

import com.hermes.hermes.framework.seguradora.domain.model.Seguradora;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeguradoraResponseDto {
    private Seguradora seguradora;
    private String token;
}
