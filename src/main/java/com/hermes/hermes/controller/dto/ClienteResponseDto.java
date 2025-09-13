package com.hermes.hermes.controller.dto;

import com.hermes.hermes.domain.model.cliente.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClienteResponseDto {
    private Cliente user;
    private String token;
}
