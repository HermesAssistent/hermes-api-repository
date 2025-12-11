package com.hermes.hermes.instancias.automotivo.domain.dtos;

import com.hermes.hermes.framework.chat.domain.dtos.FotoDto;
import com.hermes.hermes.framework.sinistro.domain.dtos.SinistroBaseDto;
import com.hermes.hermes.instancias.automotivo.domain.model.SinistroAutomotivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SinistroAutomotivoDto implements SinistroBaseDto {
    private Long id;
    private String problema;
    private String local;
    private String data;
    private String hora;
    private String modeloVeiculo;
    private String anoFabricacao;
    private String placa;
    private String danosVeiculo;
    private Boolean outrosEnvolvidos;
    private Boolean feridos;
    private Boolean possuiSeguro;
    private String seguradora;
    private String cobertura;
    private String gravidade;
    private String condicoesClimaticas;
    private String condicoesVia;
    private String testemunhas;
    private String autoridadesAcionadas;
    private String veiculoImobilizado;
    private String categoriaProblema;

    private List<FotoDto> fotos;

    public static SinistroAutomotivoDto fromEntity(SinistroAutomotivo entity) {
        if (entity == null) {
            return null;
        }
        List<FotoDto> fotoDtos = entity.getFotos() != null ? entity.getFotos().stream()
                .map(foto -> {
                    FotoDto fotoDto = new FotoDto();
                    fotoDto.setId(foto.getId());
                    fotoDto.setNomeArquivo(foto.getNomeArquivo());
                    fotoDto.setCaminhoArquivo(foto.getCaminhoArquivo());
                    fotoDto.setChatSessionId(foto.getChatSession() != null ? foto.getChatSession().getId() : null);
                    fotoDto.setSinistroId(foto.getSinistro() != null ? foto.getSinistro().getId() : null);
                    return fotoDto;
                })
                .toList() : List.of();
        return SinistroAutomotivoDto.builder()
                .id(entity.getId())
                .problema(entity.getProblema())
                .local(entity.getLocalizacao() != null ? entity.getLocalizacao().getEndereco() : null)
                .data(entity.getData())
                .hora(entity.getHora())
                .modeloVeiculo(entity.getModeloVeiculo())
                .anoFabricacao(entity.getAnoFabricacao())
                .placa(entity.getPlaca())
                .danosVeiculo(entity.getDanosVeiculo())
                .outrosEnvolvidos(entity.getOutrosEnvolvidos())
                .feridos(entity.getFeridos())
                .possuiSeguro(entity.getPossuiSeguro())
                .seguradora(entity.getSeguradora())
                .cobertura(entity.getCobertura())
                .gravidade(entity.getGravidade())
                .condicoesClimaticas(entity.getCondicoesClimaticas())
                .condicoesVia(entity.getCondicoesVia())
                .testemunhas(entity.getTestemunhas())
                .autoridadesAcionadas(entity.getAutoridadesAcionadas())
                .veiculoImobilizado(entity.getVeiculoImobilizado())
                .categoriaProblema(entity.getCategoriaProblema())
                .fotos(fotoDtos)
                .build();
    }
}
