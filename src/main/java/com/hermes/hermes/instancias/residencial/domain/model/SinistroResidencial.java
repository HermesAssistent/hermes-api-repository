package com.hermes.hermes.instancias.residencial.domain.model;

import com.hermes.hermes.framework.localizacao.domain.model.Localizacao;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import com.hermes.hermes.framework.sinistro.domain.dtos.SinistroBaseDto;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.instancias.residencial.domain.dtos.SinistroResidencialDto;
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
@Table(name = "sinistro_residencial")
public class SinistroResidencial extends SinistroBase {

    @Column(insertable=false, updatable=false)
    private String endereco;
    private String tipoImovel; // Casa, Apartamento, Sobrado, etc
    private String areaAtingida; // Cozinha, Sala, Banheiro, Telhado, etc
    private String tipoDano; // Incêndio, Alagamento, Vendaval, Roubo, etc
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

    @Embedded
    private Localizacao localizacao;

    public static SinistroResidencial fromMap(Map<String, Object> map, GeocodingService geocodingService) {
        SinistroResidencial s = new SinistroResidencial();

        if (map.containsKey("problema") && map.get("problema") != null)
            s.setProblema(map.get("problema").toString());

        if (map.containsKey("endereco") && map.get("endereco") != null) {
            String endereco = map.get("endereco").toString();
            s.setEndereco(endereco);

            // Tentar obter coordenadas do endereço
            try {
                Localizacao localizacao = geocodingService.getCoordinates(endereco);
                s.setLocalizacao(localizacao);
            } catch (Exception e) {
                System.err.println("Erro ao obter localização: " + e.getMessage());
            }
        }

        if (map.containsKey("data") && map.get("data") != null)
            s.setData(map.get("data").toString());

        if (map.containsKey("hora") && map.get("hora") != null)
            s.setHora(map.get("hora").toString());

        if (map.containsKey("tipo_imovel") && map.get("tipo_imovel") != null)
            s.setTipoImovel(map.get("tipo_imovel").toString());

        if (map.containsKey("area_atingida") && map.get("area_atingida") != null)
            s.setAreaAtingida(map.get("area_atingida").toString());

        if (map.containsKey("tipo_dano") && map.get("tipo_dano") != null)
            s.setTipoDano(map.get("tipo_dano").toString());

        if (map.containsKey("causa_provavel") && map.get("causa_provavel") != null)
            s.setCausaProvavel(map.get("causa_provavel").toString());

        if (map.containsKey("estrutura_comprometida") && map.get("estrutura_comprometida") != null)
            s.setEstruturaComprometida(Boolean.parseBoolean(map.get("estrutura_comprometida").toString()));

        if (map.containsKey("habitavel") && map.get("habitavel") != null)
            s.setHabitavel(Boolean.parseBoolean(map.get("habitavel").toString()));

        if (map.containsKey("possui_seguro") && map.get("possui_seguro") != null)
            s.setPossuiSeguro(Boolean.parseBoolean(map.get("possui_seguro").toString()));

        if (map.containsKey("seguradora") && map.get("seguradora") != null)
            s.setSeguradora(map.get("seguradora").toString());

        if (map.containsKey("cobertura") && map.get("cobertura") != null)
            s.setCobertura(map.get("cobertura").toString());

        if (map.containsKey("valor_estimado_danos") && map.get("valor_estimado_danos") != null)
            s.setValorEstimadoDanos(Double.parseDouble(map.get("valor_estimado_danos").toString()));

        if (map.containsKey("gravidade") && map.get("gravidade") != null)
            s.setGravidade(map.get("gravidade").toString());

        if (map.containsKey("testemunhas") && map.get("testemunhas") != null)
            s.setTestemunhas(map.get("testemunhas").toString());

        if (map.containsKey("autoridades_acionadas") && map.get("autoridades_acionadas") != null)
            s.setAutoridadesAcionadas(map.get("autoridades_acionadas").toString());

        if (map.containsKey("categoria_problema") && map.get("categoria_problema") != null)
            s.setCategoriaProblema(map.get("categoria_problema").toString());

        return s;
    }

    @Override
    public String toString() {
        return "SinistroResidencial{" +
                "problema='" + getProblema() + '\'' +
                ", data='" + getData() + '\'' +
                ", hora='" + getHora() + '\'' +
                ", endereco='" + endereco + '\'' +
                ", tipoImovel='" + tipoImovel + '\'' +
                ", areaAtingida='" + areaAtingida + '\'' +
                ", tipoDano='" + tipoDano + '\'' +
                ", causaProvavel='" + causaProvavel + '\'' +
                ", estruturaComprometida=" + estruturaComprometida +
                ", habitavel=" + habitavel +
                ", possuiSeguro=" + possuiSeguro +
                ", seguradora='" + seguradora + '\'' +
                ", cobertura='" + cobertura + '\'' +
                ", valorEstimadoDanos=" + valorEstimadoDanos +
                ", gravidade='" + gravidade + '\'' +
                ", testemunhas='" + testemunhas + '\'' +
                ", autoridadesAcionadas='" + autoridadesAcionadas + '\'' +
                ", categoriaProblema='" + getCategoriaProblema() + '\'' +
                '}';
    }

    @Override
    public SinistroBaseDto toDto() {
        return SinistroResidencialDto.fromEntity(this);
    }
}