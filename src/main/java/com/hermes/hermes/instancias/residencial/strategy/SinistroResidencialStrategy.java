package com.hermes.hermes.instancias.residencial.strategy;

import com.hermes.hermes.framework.exception.BusinessException;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import com.hermes.hermes.instancias.residencial.domain.model.SinistroResidencial;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SinistroResidencialStrategy implements SinistroStrategy {

    @Override
    public SinistroResidencial criarSinistro(Map<String, Object> dados, GeocodingService geocodingService) {
        SinistroResidencial sinistro = SinistroResidencial.fromMap(dados, geocodingService);
        validar(sinistro);
        return sinistro;
    }

    @Override
    public void validar(SinistroBase sinistro) {
        SinistroResidencial sinistroResidencial = (SinistroResidencial) sinistro;

        if (sinistroResidencial.getTipoImovel() == null || sinistroResidencial.getTipoImovel().isBlank()) {
            throw new BusinessException("É necessário informar o tipo de imóvel");
        }

        if (sinistroResidencial.getTipoDano() == null || sinistroResidencial.getTipoDano().isBlank()) {
            throw new BusinessException("É necessário informar o tipo de dano");
        }

        if (sinistroResidencial.getAreaAtingida() == null || sinistroResidencial.getAreaAtingida().isBlank()) {
            throw new BusinessException("É necessário informar a área atingida");
        }

        if (Boolean.TRUE.equals(sinistroResidencial.getEstruturaComprometida())
                && Boolean.TRUE.equals(sinistroResidencial.getHabitavel())) {
            throw new BusinessException(
                    "Inconsistência: imóvel com estrutura comprometida não pode ser considerado habitável"
            );
        }

        if (Boolean.TRUE.equals(sinistroResidencial.getPossuiSeguro())
                && (sinistroResidencial.getSeguradora() == null || sinistroResidencial.getSeguradora().isBlank())) {
            throw new BusinessException("É necessário informar a seguradora quando possui seguro");
        }

        if (sinistroResidencial.getProblema() == null || sinistroResidencial.getProblema().isEmpty()) {
            throw new BusinessException("É necessário informar o problema");
        }
    }

    @Override
    public TipoSinistro getTipo() {
        return TipoSinistro.RESIDENCIAL;
    }
}