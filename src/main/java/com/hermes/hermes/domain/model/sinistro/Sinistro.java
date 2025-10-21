package com.hermes.hermes.domain.model.sinistro;

import com.hermes.hermes.domain.model.abstracts.Entidade;
import com.hermes.hermes.domain.model.chat.Foto;
import com.hermes.hermes.domain.model.cliente.Cliente;
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
    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "sinistro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos;

    public static Sinistro fromMap(LinkedHashMap<String, Object> map) {
        Sinistro s = new Sinistro();
        s.setProblema((String) map.get("problema"));
        s.setLocal((String) map.get("local"));
        s.setData((String) map.get("data"));
        s.setHora((String) map.get("hora"));
        s.setModeloVeiculo((String) map.get("modelo_veiculo"));
        s.setAnoFabricacao((String) map.get("ano_fabricacao"));
        s.setPlaca((String) map.get("placa"));
        s.setDanosVeiculo((String) map.get("danos_veiculo"));
        s.setOutrosEnvolvidos((Boolean) map.get("outros_envolvidos"));
        s.setFeridos((Boolean) map.get("feridos"));
        s.setPossuiSeguro((Boolean) map.get("possui_seguro"));
        s.setSeguradora((String) map.get("seguradora"));
        s.setCobertura((String) map.get("cobertura"));
        s.setGravidade((String) map.get("gravidade"));
        s.setCondicoesClimaticas((String) map.get("condicoes_climaticas"));
        s.setCondicoesVia((String) map.get("condicoes_via"));
        s.setTestemunhas((String) map.get("testemunhas"));
        s.setAutoridadesAcionadas((String) map.get("autoridades_acionadas"));
        s.setVeiculoImobilizado((String) map.get("veiculo_imobilizado"));
        s.setCategoriaProblema((String) map.get("categoria_problema"));
        return s;
    }

    @Override
    public String toString() {
        return "Sinistro{" +
                "problema='" + problema + '\'' +
                ", local='" + local + '\'' +
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
