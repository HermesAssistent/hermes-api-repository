package com.hermes.hermes.instancias.residencial.domain.dtos;

import com.hermes.hermes.framework.chat.domain.dtos.FotoDto;
import com.hermes.hermes.framework.sinistro.domain.dtos.SinistroBaseDto;
import com.hermes.hermes.instancias.residencial.domain.model.SinistroResidencial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SinistroResidencialDto implements SinistroBaseDto {
    private Long id;
    private String problema;
    private String data;
    private String hora;
    private String categoriaProblema;

    // Campos específicos de Residencial
    private String endereco;
    private String tipoImovel;
    private String areaAtingida;
    private String tipoDano;
    private String causaProvavel;
    private Boolean estruturaComprometida;
    private Boolean habitavel;
    private Boolean possuiSeguro;
    private String seguradora;
    private String cobertura;
    private Double valorEstimadoDanos;
    private String gravidade;
    private String testemunhas;
    private String autoridadesAcionadas;

    private List<FotoDto> fotos;

    private String tipo;

    public static SinistroResidencialDto fromEntity(SinistroResidencial entity) {
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

        return SinistroResidencialDto.builder()
                .id(entity.getId())
                .problema(entity.getProblema())
                .data(entity.getData())
                .hora(entity.getHora())
                .categoriaProblema(entity.getCategoriaProblema())
                .endereco(entity.getEndereco())
                .tipoImovel(entity.getTipoImovel())
                .areaAtingida(entity.getAreaAtingida())
                .tipoDano(entity.getTipoDano())
                .causaProvavel(entity.getCausaProvavel())
                .estruturaComprometida(entity.getEstruturaComprometida())
                .habitavel(entity.getHabitavel())
                .possuiSeguro(entity.getPossuiSeguro())
                .seguradora(entity.getSeguradora())
                .cobertura(entity.getCobertura())
                .valorEstimadoDanos(entity.getValorEstimadoDanos())
                .gravidade(entity.getGravidade())
                .testemunhas(entity.getTestemunhas())
                .autoridadesAcionadas(entity.getAutoridadesAcionadas())
                .fotos(fotoDtos)
                .tipo(entity.getTipo().name())
                .build();
    }
}