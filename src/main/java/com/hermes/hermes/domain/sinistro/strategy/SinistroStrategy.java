package com.hermes.hermes.domain.sinistro.strategy;

import com.hermes.hermes.domain.model.sinistro.SinistroBase;

import java.util.Map;

public interface SinistroStrategy {
    SinistroBase criarSinistro(Map<String, Object> dados);
    void validar(SinistroBase sinistro);
    SinistroBase buscarPorId(Long id);
    String getTipo();
}

