package com.hermes.hermes.service;

import com.hermes.hermes.controller.dto.FotoDto;
import com.hermes.hermes.controller.dto.SinistroDto;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
import com.hermes.hermes.exception.FileStorageException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioService {
    private final SinistroService sinistroService;

    public byte[] gerarPdf(Long sinistroId) {

        Sinistro sinistro = sinistroService.buscarPorId(sinistroId);

        // Buscar o sinistro pelo ID
        SinistroDto sinistroDto =  SinistroDto.fromEntity(sinistro);

        // Ler o template HTML
        String html;
        try {
            html = lerTemplateHtml();
        } catch (IOException e) {
            log.error("Template HTML não encontrado: {}", e.getMessage());
            throw new FileStorageException("Erro ao ler template do relatório: " + e.getMessage());
        }

        // Preencher o HTML com os dados do sinistro
        html = preencherTemplate(html, sinistroDto);

        // Gerar PDF em memória
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();

        try {
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
        } catch (IOException e) {
            log.error("Erro ao gerar relatório PDF: {}", e.getMessage());
            throw new FileStorageException("Erro ao gerar relatório PDF: " + e.getMessage());
        }

        return os.toByteArray();
    }

    private String lerTemplateHtml() throws IOException {

        ClassPathResource resource = new ClassPathResource("templates/relatorio_sinistro.html");
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String preencherTemplate(String html, SinistroDto sinistro) {
        // Data de emissão do relatório
        String dataEmissao = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Determinar classe CSS de gravidade
        String gravidadeClass = determinarGravidadeClass(sinistro.getGravidade());


        html = html.replace("{{PROTOCOLO}}", sinistro.getId() != null ? "#" + String.format("%06d", sinistro.getId()) : "N/A");
        html = html.replace("{{DATA_EMISSAO}}", dataEmissao);
        html = html.replace("{{GRAVIDADE}}", sinistro.getGravidade() != null ? sinistro.getGravidade().toUpperCase() : "NÃO INFORMADA");
        html = html.replace("{{GRAVIDADE_CLASS}}", gravidadeClass);
        html = html.replace("{{GRAVIDADE_TEXTO}}", obterTextoGravidade(sinistro.getGravidade()));
        html = html.replace("{{PROBLEMA}}", sinistro.getProblema() != null ? sinistro.getProblema() : "N/A");
        html = html.replace("{{DATA}}", sinistro.getData() != null ? sinistro.getData() : "N/A");
        html = html.replace("{{HORA}}", sinistro.getHora() != null ? sinistro.getHora() : "N/A");
        html = html.replace("{{LOCAL}}", sinistro.getLocal() != null ? sinistro.getLocal() : "N/A");
        html = html.replace("{{CATEGORIA}}", sinistro.getCategoriaProblema() != null ? sinistro.getCategoriaProblema() : "N/A");
        html = html.replace("{{MODELO_VEICULO}}", sinistro.getModeloVeiculo() != null ? sinistro.getModeloVeiculo() : "N/A");
        html = html.replace("{{ANO_FABRICACAO}}", sinistro.getAnoFabricacao() != null ? sinistro.getAnoFabricacao() : "N/A");
        html = html.replace("{{PLACA}}", sinistro.getPlaca() != null ? sinistro.getPlaca() : "N/A");
        html = html.replace("{{DANOS_VEICULO}}", sinistro.getDanosVeiculo() != null ? sinistro.getDanosVeiculo() : "N/A");
        html = html.replace("{{VEICULO_IMOBILIZADO}}", formatarBoolean(sinistro.getVeiculoImobilizado()));
        html = html.replace("{{VEICULO_IMOBILIZADO_BADGE}}", formatarBadge(sinistro.getVeiculoImobilizado()));
        html = html.replace("{{POSSUI_SEGURO}}", formatarBoolean(sinistro.getPossuiSeguro()));
        html = html.replace("{{POSSUI_SEGURO_BADGE}}", formatarBadge(sinistro.getPossuiSeguro()));
        html = html.replace("{{SEGURADORA}}", sinistro.getSeguradora() != null ? sinistro.getSeguradora() : "N/A");
        html = html.replace("{{COBERTURA}}", sinistro.getCobertura() != null ? sinistro.getCobertura() : "N/A");
        html = html.replace("{{OUTROS_ENVOLVIDOS}}", formatarBoolean(sinistro.getOutrosEnvolvidos()));
        html = html.replace("{{OUTROS_ENVOLVIDOS_BADGE}}", formatarBadge(sinistro.getOutrosEnvolvidos()));
        html = html.replace("{{FERIDOS}}", formatarBoolean(sinistro.getFeridos()));
        html = html.replace("{{FERIDOS_BADGE}}", formatarBadge(sinistro.getFeridos()));
        html = html.replace("{{CONDICOES_CLIMATICAS}}", sinistro.getCondicoesClimaticas() != null ? sinistro.getCondicoesClimaticas() : "N/A");
        html = html.replace("{{CONDICOES_VIA}}", sinistro.getCondicoesVia() != null ? sinistro.getCondicoesVia() : "N/A");
        html = html.replace("{{TESTEMUNHAS}}", sinistro.getTestemunhas() != null ? sinistro.getTestemunhas() : "N/A");
        html = html.replace("{{AUTORIDADES}}", sinistro.getAutoridadesAcionadas() != null ? sinistro.getAutoridadesAcionadas() : "N/A");

        String fotosHtml = gerarHtmlFotosBase64(sinistro.getFotos());
        html = html.replace("{{FOTOS}}", fotosHtml);

        return html;
    }

    private String gerarHtmlFotosBase64(List<FotoDto> fotos) {
        if (fotos == null || fotos.isEmpty()) {
            return "<p style='color:#64748b; font-size:10pt;'>Nenhuma foto anexada ao relatório.</p>";
        }

        StringBuilder sb = new StringBuilder();
        for (FotoDto foto : fotos) {
            try {
                Path caminho = Paths.get(foto.getCaminhoArquivo());
                byte[] bytes = Files.readAllBytes(caminho);
                String base64 = Base64.getEncoder().encodeToString(bytes);

                String contentType = Files.probeContentType(caminho);
                if (contentType == null) contentType = "image/jpeg";

                String nome = foto.getNomeArquivo() != null ? foto.getNomeArquivo() : "Foto";

                sb.append("<div class='photo-card'>")
                        .append("<img src='data:").append(contentType).append(";base64,")
                        .append(base64)
                        .append("' alt='").append(nome).append("'/>")
                        .append("<p class='caption'>").append(nome).append("</p>")
                        .append("</div>");
            } catch (IOException e) {
                sb.append("<p style='color:#991b1b;'>Erro ao carregar a foto: ")
                        .append(foto.getNomeArquivo()).append("</p>");
            }
        }

        return sb.toString();
    }

    private String determinarGravidadeClass(String gravidade) {
        if (gravidade == null) return "media";

        switch (gravidade.toLowerCase()) {
            case "alta":
                return "alta";
            case "baixa":
                return "baixa";
            case "média":
            case "media":
                return "media";
            default:
                return "media";
        }
    }

    private String obterTextoGravidade(String gravidade) {
        if (gravidade == null) return "Este sinistro requer análise detalhada.";

        switch (gravidade.toLowerCase()) {
            case "alta":
                return "Este sinistro foi classificado como de alta gravidade e requer atenção prioritária.";
            case "baixa":
                return "Este sinistro foi classificado como de baixa gravidade.";
            case "média":
            case "media":
                return "Este sinistro foi classificado como de média gravidade e requer análise.";
            default:
                return "Este sinistro requer análise detalhada.";
        }
    }

    private String formatarBoolean(Object valor) {
        if (valor == null) return "N/A";

        if (valor instanceof Boolean) {
            return ((Boolean) valor) ? "SIM" : "NÃO";
        }

        if (valor instanceof String) {
            String str = ((String) valor).toLowerCase();
            if (str.equals("sim") || str.equals("true") || str.equals("1")) {
                return "SIM";
            } else if (str.equals("não") || str.equals("nao") || str.equals("false") || str.equals("0")) {
                return "NÃO";
            }
        }

        return valor.toString();
    }

    private String formatarBadge(Object valor) {
        if (valor == null) return "";

        boolean isTrue = false;

        if (valor instanceof Boolean) {
            isTrue = (Boolean) valor;
        } else if (valor instanceof String) {
            String str = ((String) valor).toLowerCase();
            isTrue = str.equals("sim") || str.equals("true") || str.equals("1");
        }

        return isTrue ? "sim" : "nao";
    }

}
