package com.hermes.hermes.instancias.transporte.domain.model;
import com.hermes.hermes.framework.localizacao.domain.model.Localizacao;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import com.hermes.hermes.framework.sinistro.domain.dtos.SinistroBaseDto;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.instancias.transporte.domain.dtos.SinistroTransporteDto;
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
@Table(name = "sinistro_carga")
public class SinistroTransporte extends SinistroBase {

    private String numeroNotaFiscal;
    private String tipoCarga; // Perecível, Frágil, Granel, Container, etc
    private String descricaoCarga;
    private Double pesoCarga; // em kg
    private Double valorCarga;
    private String transportadora;
    private String nomeMotorista;
    private String cpfMotorista;
    private String placaVeiculo;
    private String tipoVeiculo; // Caminhão, Carreta, Van, etc
    private String tipoOcorrencia; // Roubo, Acidente, Avaria, Extravio, etc
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

    @Embedded
    private Localizacao localizacao;

    public static SinistroTransporte fromMap(Map<String, Object> map, GeocodingService geocodingService) {
        SinistroTransporte s = new SinistroTransporte();

        if (map.containsKey("problema") && map.get("problema") != null)
            s.setProblema(map.get("problema").toString());

        if (map.containsKey("local") && map.get("local") != null) {
            String local = map.get("local").toString();

            try {
                Localizacao localizacao = geocodingService.getCoordinates(local);
                s.setLocalizacao(localizacao);
            } catch (Exception e) {
                System.err.println("Erro ao obter localização: " + e.getMessage());
                Localizacao localizacao = new Localizacao();
                localizacao.setEndereco(local);
                s.setLocalizacao(localizacao);
            }
        }

        if (map.containsKey("data") && map.get("data") != null)
            s.setData(map.get("data").toString());

        if (map.containsKey("hora") && map.get("hora") != null)
            s.setHora(map.get("hora").toString());

        if (map.containsKey("numero_nota_fiscal") && map.get("numero_nota_fiscal") != null)
            s.setNumeroNotaFiscal(map.get("numero_nota_fiscal").toString());

        if (map.containsKey("tipo_carga") && map.get("tipo_carga") != null)
            s.setTipoCarga(map.get("tipo_carga").toString());

        if (map.containsKey("descricao_carga") && map.get("descricao_carga") != null)
            s.setDescricaoCarga(map.get("descricao_carga").toString());

        if (map.containsKey("peso_carga") && map.get("peso_carga") != null)
            s.setPesoCarga(Double.parseDouble(map.get("peso_carga").toString()));

        if (map.containsKey("valor_carga") && map.get("valor_carga") != null)
            s.setValorCarga(Double.parseDouble(map.get("valor_carga").toString()));

        if (map.containsKey("transportadora") && map.get("transportadora") != null)
            s.setTransportadora(map.get("transportadora").toString());

        if (map.containsKey("nome_motorista") && map.get("nome_motorista") != null)
            s.setNomeMotorista(map.get("nome_motorista").toString());

        if (map.containsKey("cpf_motorista") && map.get("cpf_motorista") != null)
            s.setCpfMotorista(map.get("cpf_motorista").toString());

        if (map.containsKey("placa_veiculo") && map.get("placa_veiculo") != null)
            s.setPlacaVeiculo(map.get("placa_veiculo").toString());

        if (map.containsKey("tipo_veiculo") && map.get("tipo_veiculo") != null)
            s.setTipoVeiculo(map.get("tipo_veiculo").toString());

        if (map.containsKey("tipo_ocorrencia") && map.get("tipo_ocorrencia") != null)
            s.setTipoOcorrencia(map.get("tipo_ocorrencia").toString());

        if (map.containsKey("perda_total") && map.get("perda_total") != null)
            s.setPerdaTotal(Boolean.parseBoolean(map.get("perda_total").toString()));

        if (map.containsKey("percentual_perda") && map.get("percentual_perda") != null)
            s.setPercentualPerda(Double.parseDouble(map.get("percentual_perda").toString()));

        if (map.containsKey("carga_recuperada") && map.get("carga_recuperada") != null)
            s.setCargaRecuperada(Boolean.parseBoolean(map.get("carga_recuperada").toString()));

        if (map.containsKey("possui_seguro") && map.get("possui_seguro") != null)
            s.setPossuiSeguro(Boolean.parseBoolean(map.get("possui_seguro").toString()));

        if (map.containsKey("seguradora") && map.get("seguradora") != null)
            s.setSeguradora(map.get("seguradora").toString());

        if (map.containsKey("numero_apolice") && map.get("numero_apolice") != null)
            s.setNumeroApolice(map.get("numero_apolice").toString());

        if (map.containsKey("origem") && map.get("origem") != null)
            s.setOrigem(map.get("origem").toString());

        if (map.containsKey("destino") && map.get("destino") != null)
            s.setDestino(map.get("destino").toString());

        if (map.containsKey("gravidade") && map.get("gravidade") != null)
            s.setGravidade(map.get("gravidade").toString());

        if (map.containsKey("testemunhas") && map.get("testemunhas") != null)
            s.setTestemunhas(map.get("testemunhas").toString());

        if (map.containsKey("autoridades_acionadas") && map.get("autoridades_acionadas") != null)
            s.setAutoridadesAcionadas(map.get("autoridades_acionadas").toString());

        if (map.containsKey("categoria_problema") && map.get("categoria_problema") != null)
            s.setCategoriaProblema(map.get("categoria_problema").toString());

        s.setTipo(TipoSinistro.TRANSPORTE);

        return s;
    }

    @Override
    public String toString() {
        return "SinistroCarga{" +
                "problema='" + getProblema() + '\'' +
                ", data='" + getData() + '\'' +
                ", hora='" + getHora() + '\'' +
                ", local='" + getLocalizacao() + '\'' +
                ", numeroNotaFiscal='" + numeroNotaFiscal + '\'' +
                ", tipoCarga='" + tipoCarga + '\'' +
                ", descricaoCarga='" + descricaoCarga + '\'' +
                ", pesoCarga=" + pesoCarga +
                ", valorCarga=" + valorCarga +
                ", transportadora='" + transportadora + '\'' +
                ", nomeMotorista='" + nomeMotorista + '\'' +
                ", cpfMotorista='" + cpfMotorista + '\'' +
                ", placaVeiculo='" + placaVeiculo + '\'' +
                ", tipoVeiculo='" + tipoVeiculo + '\'' +
                ", tipoOcorrencia='" + tipoOcorrencia + '\'' +
                ", perdaTotal=" + perdaTotal +
                ", percentualPerda=" + percentualPerda +
                ", cargaRecuperada=" + cargaRecuperada +
                ", possuiSeguro=" + possuiSeguro +
                ", seguradora='" + seguradora + '\'' +
                ", numeroApolice='" + numeroApolice + '\'' +
                ", origem='" + origem + '\'' +
                ", destino='" + destino + '\'' +
                ", gravidade='" + gravidade + '\'' +
                ", testemunhas='" + testemunhas + '\'' +
                ", autoridadesAcionadas='" + autoridadesAcionadas + '\'' +
                ", categoriaProblema='" + getCategoriaProblema() + '\'' +
                ", tipoSinistro='" + getTipo() + '\'' +
                '}';
    }

    @Override
    public SinistroBaseDto toDto() {
        return SinistroTransporteDto.fromEntity(this);
    }
}