package com.hermes.hermes.instancias.automotivo.domain.model;

import com.hermes.hermes.instancias.automotivo.domain.dtos.SinistroAutomotivoDto;
import com.hermes.hermes.framework.sinistro.domain.dtos.SinistroBaseDto;
import com.hermes.hermes.framework.localizacao.domain.model.Localizacao;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sinistro_automotivo")
public class SinistroAutomotivo extends SinistroBase {
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

    @Embedded
    private Localizacao localizacao;

    public void fromMap(Map<String, Object> map, GeocodingService geocodingService) {
        if (map.containsKey("problema") && map.get("problema") != null)
            this.setProblema(map.get("problema").toString());

        if (map.containsKey("local") && map.get("local") != null) {
            String cep = map.get("local").toString();
            try {
                Localizacao localizacao = geocodingService.getCoordinates(cep);
                this.setLocalizacao(localizacao);
            } catch (Exception e) {
                System.err.println("Erro ao obter localização: " + e.getMessage());
            }
        }

        if (map.containsKey("data") && map.get("data") != null)
            this.setData(map.get("data").toString());

        if (map.containsKey("hora") && map.get("hora") != null)
            this.setHora(map.get("hora").toString());

        if (map.containsKey("modelo_veiculo") && map.get("modelo_veiculo") != null)
            this.setModeloVeiculo(map.get("modelo_veiculo").toString());

        if (map.containsKey("ano_fabricacao") && map.get("ano_fabricacao") != null)
            this.setAnoFabricacao(map.get("ano_fabricacao").toString());

        if (map.containsKey("placa") && map.get("placa") != null)
            this.setPlaca(map.get("placa").toString());

        if (map.containsKey("danos_veiculo") && map.get("danos_veiculo") != null)
            this.setDanosVeiculo(map.get("danos_veiculo").toString());

        if (map.containsKey("outros_envolvidos") && map.get("outros_envolvidos") != null)
            this.setOutrosEnvolvidos(Boolean.parseBoolean(map.get("outros_envolvidos").toString()));

        if (map.containsKey("feridos") && map.get("feridos") != null)
            this.setFeridos(Boolean.parseBoolean(map.get("feridos").toString()));

        if (map.containsKey("possui_seguro") && map.get("possui_seguro") != null)
            this.setPossuiSeguro(Boolean.parseBoolean(map.get("possui_seguro").toString()));

        if (map.containsKey("seguradora") && map.get("seguradora") != null)
            this.setSeguradora(map.get("seguradora").toString());

        if (map.containsKey("cobertura") && map.get("cobertura") != null)
            this.setCobertura(map.get("cobertura").toString());

        if (map.containsKey("gravidade") && map.get("gravidade") != null)
            this.setGravidade(map.get("gravidade").toString());

        if (map.containsKey("condicoes_climaticas") && map.get("condicoes_climaticas") != null)
            this.setCondicoesClimaticas(map.get("condicoes_climaticas").toString());

        if (map.containsKey("condicoes_via") && map.get("condicoes_via") != null)
            this.setCondicoesVia(map.get("condicoes_via").toString());

        if (map.containsKey("testemunhas") && map.get("testemunhas") != null)
            this.setTestemunhas(map.get("testemunhas").toString());

        if (map.containsKey("autoridades_acionadas") && map.get("autoridades_acionadas") != null)
            this.setAutoridadesAcionadas(map.get("autoridades_acionadas").toString());

        if (map.containsKey("veiculo_imobilizado") && map.get("veiculo_imobilizado") != null)
            this.setVeiculoImobilizado(map.get("veiculo_imobilizado").toString());

        if (map.containsKey("categoria_problema") && map.get("categoria_problema") != null)
            this.setCategoriaProblema(map.get("categoria_problema").toString());
    }


    @Override
    public String toString() {
        return "Sinistro{" +
                "problema='" + getProblema() + '\'' +
                ", data='" + getData() + '\'' +
                ", hora='" + getHora() + '\'' +
                ", modeloVeiculo='" + modeloVeiculo + '\'' +
                ", anoFabricacao='" + anoFabricacao + '\'' +
                ", placa='" + placa + '\'' +
                ", danosVeiculo='" + danosVeiculo + '\'' +
                ", outrosEnvolvidos=" + outrosEnvolvidos +
                ", feridos=" + feridos +
                ", possuiSeguro=" + possuiSeguro +
                ", seguradora='" + seguradora + '\'' +
                ", cobertura='" + cobertura + '\'' +
                ", gravidade='" + gravidade + '\'' +
                ", condicoesClimaticas='" + condicoesClimaticas + '\'' +
                ", condicoesVia='" + condicoesVia + '\'' +
                ", testemunhas='" + testemunhas + '\'' +
                ", autoridadesAcionadas='" + autoridadesAcionadas + '\'' +
                ", veiculoImobilizado='" + veiculoImobilizado + '\'' +
                ", categoriaProblema='" + getCategoriaProblema() + '\'' +
                '}';
    }

    @Override
    public SinistroBaseDto toDto() {
        return SinistroAutomotivoDto.fromEntity(this);
    }
}