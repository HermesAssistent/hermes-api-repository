package com.hermes.hermes.relatorio.controller;

import com.hermes.hermes.relatorio.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("v1/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/sinistro/{id}/{tipoSinistro}")
    public ResponseEntity<byte[]> gerarRelatorio(
            @PathVariable Long id,
            @PathVariable String tipoSinistro
    ) {
        log.info("Requisição de relatório recebida - ID: {}, Tipo: {}", id, tipoSinistro);

        try {
            byte[] pdfBytes = relatorioService.gerarPdf(id, tipoSinistro);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                    "filename",
                    String.format("relatorio-sinistro-%s-%d.pdf", tipoSinistro, id)
            );
            headers.setContentLength(pdfBytes.length);

            log.info("Relatório gerado com sucesso - Tamanho: {} bytes", pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (IllegalArgumentException e) {
            log.error("Tipo de sinistro inválido: {}", tipoSinistro);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);

        } catch (Exception e) {
            log.error("Erro ao gerar relatório: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}