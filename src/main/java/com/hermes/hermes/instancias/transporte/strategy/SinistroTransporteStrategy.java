package com.hermes.hermes.instancias.transporte.strategy;

import com.hermes.hermes.framework.exception.BusinessException;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import com.hermes.hermes.instancias.transporte.domain.model.SinistroTransporte;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SinistroTransporteStrategy implements SinistroStrategy {

    @Override
    public SinistroTransporte criarSinistro(Map<String, Object> dados, GeocodingService geocodingService) {
        SinistroTransporte sinistro = SinistroTransporte.fromMap(dados, geocodingService);
        validar(sinistro);
        return sinistro;
    }

    @Override
    public void validar(SinistroBase sinistro) {
        SinistroTransporte SinistroTransporte = (SinistroTransporte) sinistro;

        // Validações obrigatórias
        if (SinistroTransporte.getTipoCarga() == null || SinistroTransporte.getTipoCarga().isBlank()) {
            throw new BusinessException("É necessário informar o tipo de carga");
        }

        if (SinistroTransporte.getTipoOcorrencia() == null || SinistroTransporte.getTipoOcorrencia().isBlank()) {
            throw new BusinessException("É necessário informar o tipo de ocorrência");
        }

        if (SinistroTransporte.getTransportadora() == null || SinistroTransporte.getTransportadora().isBlank()) {
            throw new BusinessException("É necessário informar a transportadora");
        }

        if (SinistroTransporte.getPlacaVeiculo() == null || SinistroTransporte.getPlacaVeiculo().isBlank()) {
            throw new BusinessException("É necessário informar a placa do veículo");
        }

        // Validações de consistência
        if (SinistroTransporte.getValorCarga() != null && SinistroTransporte.getValorCarga() <= 0) {
            throw new BusinessException("O valor da carga deve ser maior que zero");
        }

        if (SinistroTransporte.getPesoCarga() != null && SinistroTransporte.getPesoCarga() <= 0) {
            throw new BusinessException("O peso da carga deve ser maior que zero");
        }

        // Validação de perda total vs percentual
        if (Boolean.TRUE.equals(SinistroTransporte.getPerdaTotal())) {
            if (SinistroTransporte.getPercentualPerda() != null && SinistroTransporte.getPercentualPerda() < 75.0) {
                throw new BusinessException(
                        "Perda total requer percentual de perda igual ou superior a 75%"
                );
            }
        }

        // Se tem seguro, deve informar a seguradora
        if (Boolean.TRUE.equals(SinistroTransporte.getPossuiSeguro())) {
            if (SinistroTransporte.getSeguradora() == null || SinistroTransporte.getSeguradora().isBlank()) {
                throw new BusinessException("É necessário informar a seguradora quando possui seguro");
            }
            if (SinistroTransporte.getNumeroApolice() == null || SinistroTransporte.getNumeroApolice().isBlank()) {
                throw new BusinessException("É necessário informar o número da apólice quando possui seguro");
            }
        }

        // Validação de origem e destino
        if (SinistroTransporte.getOrigem() == null || SinistroTransporte.getOrigem().isBlank()) {
            throw new BusinessException("É necessário informar a origem da carga");
        }

        if (SinistroTransporte.getDestino() == null || SinistroTransporte.getDestino().isBlank()) {
            throw new BusinessException("É necessário informar o destino da carga");
        }

        // Validação de percentual de perda
        if (SinistroTransporte.getPercentualPerda() != null) {
            if (SinistroTransporte.getPercentualPerda() < 0 || SinistroTransporte.getPercentualPerda() > 100) {
                throw new BusinessException("O percentual de perda deve estar entre 0 e 100");
            }
        }
    }

    @Override
    public TipoSinistro getTipo() {
        return TipoSinistro.TRANSPORTE;
    }
}
