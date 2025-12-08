package com.hermes.hermes.framework.relatorio.service;

import com.hermes.hermes.framework.exception.FileStorageException;
import com.hermes.hermes.framework.relatorio.domain.strategy.GeradorRelatorioStrategy;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.sinistro.service.SinistroService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final SinistroService sinistroService;
    private final GeradorRelatorioFactory geradorFactory;

    /**
     * Gera relatório PDF para qualquer tipo de sinistro
     * @param sinistroId ID do sinistro
     * @param tipoSinistro tipo do sinistro (automotivo, residencial, carga)
     * @return bytes do PDF gerado
     */
    public byte[] gerarPdf(Long sinistroId, String tipoSinistro) {
        log.info("Gerando relatório para sinistro ID: {} do tipo: {}", sinistroId, tipoSinistro);

        // Buscar sinistro
        SinistroBase sinistro = sinistroService.buscarPorId(sinistroId, tipoSinistro);

        // Obter estratégia apropriada
        GeradorRelatorioStrategy strategy = geradorFactory.obterStrategy(tipoSinistro);

        // Ler template HTML
        String templateHtml = lerTemplateHtml(strategy.getNomeTemplate());

        // Gerar HTML preenchido usando a estratégia
        String htmlPreenchido = strategy.gerarHtml(sinistro);

        // Se a estratégia retornou template placeholder, substitui pelo template real
        if (htmlPreenchido.contains("{{TEMPLATE}}")) {
            htmlPreenchido = htmlPreenchido.replace("{{TEMPLATE}}", templateHtml);
        } else {
            // Caso já tenha processado o template dentro da estratégia
            htmlPreenchido = templateHtml.replace("{{TEMPLATE}}", htmlPreenchido);
        }

        // Gerar PDF
        return gerarPdfFromHtml(htmlPreenchido);
    }

    /**
     * Lê o template HTML do classpath
     */
    private String lerTemplateHtml(String templatePath) {
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            try (InputStream inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("Template HTML não encontrado: {} - {}", templatePath, e.getMessage());
            throw new FileStorageException("Erro ao ler template do relatório: " + e.getMessage());
        }
    }

    /**
     * Gera PDF a partir do HTML
     */
    private byte[] gerarPdfFromHtml(String html) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();

        try {
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            log.info("PDF gerado com sucesso. Tamanho: {} bytes", outputStream.size());
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Erro ao gerar relatório PDF: {}", e.getMessage());
            throw new FileStorageException("Erro ao gerar relatório PDF: " + e.getMessage());
        }
    }
}