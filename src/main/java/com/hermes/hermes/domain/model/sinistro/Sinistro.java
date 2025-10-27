package com.hermes.hermes.domain.model.sinistro;

import com.hermes.hermes.domain.model.abstracts.Entidade;
import com.hermes.hermes.domain.model.chat.Foto;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.domain.model.localizacao.Localizacao;
import com.hermes.hermes.service.GeocodingService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Sinistro extends Entidade {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sin_sinistro_seq")
    @SequenceGenerator(name = "sin_sinistro_seq", sequenceName = "sin_sinistro_seq", allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    private String problema;
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

    @OneToMany(mappedBy = "sinistro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos;
    @Embedded
    private Localizacao localizacao;

    public static Sinistro fromMap(LinkedHashMap<String, Object> map, GeocodingService geocodingService) {
        Sinistro s = new Sinistro();

        if (map.containsKey("problema") && map.get("problema") != null)
            s.setProblema(map.get("problema").toString());

        if (map.containsKey("local") && map.get("local") != null) {
            String cep = map.get("local").toString();
            try {
                Localizacao localizacao = geocodingService.getCoordinates(cep);
                s.setLocalizacao(localizacao);
            } catch (Exception e) {
                System.err.println("Erro ao obter localização: " + e.getMessage());
            }
        }

        if (map.containsKey("data") && map.get("data") != null)
            s.setData(map.get("data").toString());

        if (map.containsKey("hora") && map.get("hora") != null)
            s.setHora(map.get("hora").toString());

        if (map.containsKey("modelo_veiculo") && map.get("modelo_veiculo") != null)
            s.setModeloVeiculo(map.get("modelo_veiculo").toString());

        if (map.containsKey("ano_fabricacao") && map.get("ano_fabricacao") != null)
            s.setAnoFabricacao(map.get("ano_fabricacao").toString());

        if (map.containsKey("placa") && map.get("placa") != null)
            s.setPlaca(map.get("placa").toString());

        if (map.containsKey("danos_veiculo") && map.get("danos_veiculo") != null)
            s.setDanosVeiculo(map.get("danos_veiculo").toString());

        if (map.containsKey("outros_envolvidos") && map.get("outros_envolvidos") != null)
            s.setOutrosEnvolvidos(Boolean.parseBoolean(map.get("outros_envolvidos").toString()));

        if (map.containsKey("feridos") && map.get("feridos") != null)
            s.setFeridos(Boolean.parseBoolean(map.get("feridos").toString()));

        if (map.containsKey("possui_seguro") && map.get("possui_seguro") != null)
            s.setPossuiSeguro(Boolean.parseBoolean(map.get("possui_seguro").toString()));

        if (map.containsKey("seguradora") && map.get("seguradora") != null)
            s.setSeguradora(map.get("seguradora").toString());

        if (map.containsKey("cobertura") && map.get("cobertura") != null)
            s.setCobertura(map.get("cobertura").toString());

        if (map.containsKey("gravidade") && map.get("gravidade") != null)
            s.setGravidade(map.get("gravidade").toString());

        if (map.containsKey("condicoes_climaticas") && map.get("condicoes_climaticas") != null)
            s.setCondicoesClimaticas(map.get("condicoes_climaticas").toString());

        if (map.containsKey("condicoes_via") && map.get("condicoes_via") != null)
            s.setCondicoesVia(map.get("condicoes_via").toString());

        if (map.containsKey("testemunhas") && map.get("testemunhas") != null)
            s.setTestemunhas(map.get("testemunhas").toString());

        if (map.containsKey("autoridades_acionadas") && map.get("autoridades_acionadas") != null)
            s.setAutoridadesAcionadas(map.get("autoridades_acionadas").toString());

        if (map.containsKey("veiculo_imobilizado") && map.get("veiculo_imobilizado") != null)
            s.setVeiculoImobilizado(map.get("veiculo_imobilizado").toString());

        if (map.containsKey("categoria_problema") && map.get("categoria_problema") != null)
            s.setCategoriaProblema(map.get("categoria_problema").toString());

        return s;
    }


    @Override
    public String toString() {
        return "Sinistro{" +
                "problema='" + problema + '\'' +
                ", data='" + data + '\'' +
                ", hora='" + hora + '\'' +
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
                ", categoriaProblema='" + categoriaProblema + '\'' +
                '}';
    }
}