package com.hermes.hermes.controller.dto;

import com.hermes.hermes.domain.model.sinistro.Sinistro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SinistroDto {
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

    public static SinistroDto fromEntity(Sinistro entity) {
        if (entity == null) {
            return null;
        }

        return SinistroDto.builder()
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
                .build();
    }
}
