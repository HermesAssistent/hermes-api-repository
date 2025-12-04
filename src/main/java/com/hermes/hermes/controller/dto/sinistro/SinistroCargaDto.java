package com.hermes.hermes.controller.dto.sinistro;

import com.hermes.hermes.controller.dto.FotoDto;
import com.hermes.hermes.domain.model.sinistro.SinistroCarga;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SinistroCargaDto implements SinistroBaseDto {
    private Long id;
    private String problema;
    private String local;
    private String data;
    private String hora;
    private String categoriaProblema;

    // Campos específicos de Carga
    private String numeroNotaFiscal;
    private String tipoCarga;
    private String descricaoCarga;
    private Double pesoCarga;
    private Double valorCarga;
    private String transportadora;
    private String nomeMotorista;
    private String cpfMotorista;
    private String placaVeiculo;
    private String tipoVeiculo;
    private String tipoOcorrencia;
    private Boolean perdaTotal;
    private Double percentualPerda;
    private Boolean cargaRecuperada;
    private Boolean possuiSeguro;
    private String seguradora;
    private String numeroApolice;
    private String origem;
    private String destino;
    private String gravidade;
    private String testemunhas;
    private String autoridadesAcionadas;

    private List<FotoDto> fotos;

    public static SinistroCargaDto fromEntity(SinistroCarga entity) {
        if (entity == null) {
            return null;
        }

        List<FotoDto> fotoDtos = entity.getFotos().stream()
                .map(foto -> {
                    FotoDto fotoDto = new FotoDto();
                    fotoDto.setId(foto.getId());
                    fotoDto.setNomeArquivo(foto.getNomeArquivo());
                    fotoDto.setCaminhoArquivo(foto.getCaminhoArquivo());
                    fotoDto.setChatSessionId(foto.getChatSession() != null ? foto.getChatSession().getId() : null);
                    fotoDto.setSinistroId(foto.getSinistro() != null ? foto.getSinistro().getId() : null);
                    return fotoDto;
                })
                .toList();

        return SinistroCargaDto.builder()
                .id(entity.getId())
                .problema(entity.getProblema())
                .local(null)
                .data(entity.getData())
                .hora(entity.getHora())
                .categoriaProblema(entity.getCategoriaProblema())
                .numeroNotaFiscal(entity.getNumeroNotaFiscal())
                .tipoCarga(entity.getTipoCarga())
                .descricaoCarga(entity.getDescricaoCarga())
                .pesoCarga(entity.getPesoCarga())
                .valorCarga(entity.getValorCarga())
                .transportadora(entity.getTransportadora())
                .nomeMotorista(entity.getNomeMotorista())
                .cpfMotorista(entity.getCpfMotorista())
                .placaVeiculo(entity.getPlacaVeiculo())
                .tipoVeiculo(entity.getTipoVeiculo())
                .tipoOcorrencia(entity.getTipoOcorrencia())
                .perdaTotal(entity.getPerdaTotal())
                .percentualPerda(entity.getPercentualPerda())
                .cargaRecuperada(entity.getCargaRecuperada())
                .possuiSeguro(entity.getPossuiSeguro())
                .seguradora(entity.getSeguradora())
                .numeroApolice(entity.getNumeroApolice())
                .origem(entity.getOrigem())
                .destino(entity.getDestino())
                .gravidade(entity.getGravidade())
                .testemunhas(entity.getTestemunhas())
                .autoridadesAcionadas(entity.getAutoridadesAcionadas())
                .fotos(fotoDtos)
                .build();
    }
}