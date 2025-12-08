package com.hermes.hermes.framework.chat.domain.dtos;

import lombok.Data;

@Data
public class FotoDto {
    private Long id;
    private String nomeArquivo;
    private String caminhoArquivo;
    private Long chatSessionId;
    private Long sinistroId;
}
