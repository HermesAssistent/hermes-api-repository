package com.hermes.hermes.framework.sinistro.domain.strategy;

import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;

import java.util.Map;

public interface SinistroStrategy {
    SinistroBase criarSinistro(Map<String, Object> dados, GeocodingService geocodingService);
    void validar(SinistroBase sinistro);
    TipoSinistro getTipo();
}

