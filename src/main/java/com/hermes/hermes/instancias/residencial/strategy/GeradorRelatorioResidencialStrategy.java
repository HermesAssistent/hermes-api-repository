package com.hermes.hermes.instancias.residencial.strategy;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.instancias.residencial.domain.dtos.SinistroResidencialDto;
import com.hermes.hermes.instancias.residencial.domain.model.SinistroResidencial;
import com.hermes.hermes.framework.chat.domain.dtos.FotoDto;
import com.hermes.hermes.framework.relatorio.domain.strategy.GeradorRelatorioStrategy;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class GeradorRelatorioResidencialStrategy implements GeradorRelatorioStrategy {

    @Override
    public boolean suporta(TipoSinistro tipoSinistro) {
        return tipoSinistro.equals(TipoSinistro.RESIDENCIAL);
    }

    @Override
    public String getNomeTemplate() {
        return "templates/relatorio_sinistro_residencial.html";
    }

    @Override
    public String gerarHtml(SinistroBase sinistro) {
        SinistroResidencial sinistroResidencial = (SinistroResidencial) sinistro;
        SinistroResidencialDto dto = SinistroResidencialDto.fromEntity(sinistroResidencial);

        String html = "{{TEMPLATE}}"; // Será substituído pelo template real
        return preencherTemplate(html, dto);
    }

    private String preencherTemplate(String html, SinistroResidencialDto sinistro) {
        String dataEmissao = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String gravidadeClass = determinarGravidadeClass(sinistro.getGravidade());

        html = html.replace("{{PROTOCOLO}}", sinistro.getId() != null ? "#" + String.format("%06d", sinistro.getId()) : "N/A");
        html = html.replace("{{DATA_EMISSAO}}", dataEmissao);
        html = html.replace("{{GRAVIDADE}}", sinistro.getGravidade() != null ? sinistro.getGravidade().toUpperCase() : "NÃO INFORMADA");
        html = html.replace("{{GRAVIDADE_CLASS}}", gravidadeClass);
        html = html.replace("{{GRAVIDADE_TEXTO}}", obterTextoGravidade(sinistro.getGravidade()));
        html = html.replace("{{PROBLEMA}}", sinistro.getProblema() != null ? sinistro.getProblema() : "N/A");
        html = html.replace("{{DATA}}", sinistro.getData() != null ? sinistro.getData() : "N/A");
        html = html.replace("{{HORA}}", sinistro.getHora() != null ? sinistro.getHora() : "N/A");

        // Campos específicos de residencial
        html = html.replace("{{ENDERECO}}", sinistro.getEndereco() != null ? sinistro.getEndereco() : "N/A");
        html = html.replace("{{TIPO_IMOVEL}}", sinistro.getTipoImovel() != null ? sinistro.getTipoImovel() : "N/A");
        html = html.replace("{{AREA_ATINGIDA}}", sinistro.getAreaAtingida() != null ? sinistro.getAreaAtingida() : "N/A");
        html = html.replace("{{TIPO_DANO}}", sinistro.getTipoDano() != null ? sinistro.getTipoDano() : "N/A");
        html = html.replace("{{CAUSA_PROVAVEL}}", sinistro.getCausaProvavel() != null ? sinistro.getCausaProvavel() : "N/A");
        html = html.replace("{{ESTRUTURA_COMPROMETIDA}}", formatarBoolean(sinistro.getEstruturaComprometida()));
        html = html.replace("{{ESTRUTURA_COMPROMETIDA_BADGE}}", formatarBadge(sinistro.getEstruturaComprometida()));
        html = html.replace("{{HABITAVEL}}", formatarBoolean(sinistro.getHabitavel()));
        html = html.replace("{{HABITAVEL_BADGE}}", formatarBadge(sinistro.getHabitavel()));
        html = html.replace("{{POSSUI_SEGURO}}", formatarBoolean(sinistro.getPossuiSeguro()));
        html = html.replace("{{POSSUI_SEGURO_BADGE}}", formatarBadge(sinistro.getPossuiSeguro()));
        html = html.replace("{{SEGURADORA}}", sinistro.getSeguradora() != null ? sinistro.getSeguradora() : "N/A");
        html = html.replace("{{COBERTURA}}", sinistro.getCobertura() != null ? sinistro.getCobertura() : "N/A");
        html = html.replace("{{VALOR_ESTIMADO}}", sinistro.getValorEstimadoDanos() != null ? formatarValor(sinistro.getValorEstimadoDanos()) : "N/A");
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

    private String formatarValor(Double valor) {
        return String.format("R$ %.2f", valor);
    }

    private String determinarGravidadeClass(String gravidade) {
        if (gravidade == null) return "media";
        switch (gravidade.toLowerCase()) {
            case "alta": return "alta";
            case "baixa": return "baixa";
            case "média":
            case "media": return "media";
            default: return "media";
        }
    }

    private String obterTextoGravidade(String gravidade) {
        if (gravidade == null) return "Este sinistro requer análise detalhada.";
        switch (gravidade.toLowerCase()) {
            case "alta": return "Este sinistro foi classificado como de alta gravidade e requer atenção prioritária.";
            case "baixa": return "Este sinistro foi classificado como de baixa gravidade.";
            case "média":
            case "media": return "Este sinistro foi classificado como de média gravidade e requer análise.";
            default: return "Este sinistro requer análise detalhada.";
        }
    }

    private String formatarBoolean(Object valor) {
        if (valor == null) return "N/A";
        if (valor instanceof Boolean) {
            return ((Boolean) valor) ? "SIM" : "NÃO";
        }
        if (valor instanceof String) {
            String str = ((String) valor).toLowerCase();
            if (str.equals("sim") || str.equals("true") || str.equals("1")) return "SIM";
            if (str.equals("não") || str.equals("nao") || str.equals("false") || str.equals("0")) return "NÃO";
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