package com.hermes.hermes.controller.dto;

import lombok.Data;

@Data
public class FotoDto {
    private Long id;
    private String nomeArquivo;
    private String caminhoArquivo;
    private Long chatSessionId;
    private Long sinistroId;
}
