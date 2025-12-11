package com.hermes.hermes.framework.sinistro.service;

import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SinistroFactory {
    private final Map<TipoSinistro, SinistroStrategy> strategies = new HashMap<>();

    @Autowired
    public SinistroFactory(List<SinistroStrategy> strategiesList) {
        System.out.println("Strategies encontradas:");
        strategiesList.forEach(s -> System.out.println("- " + s.getTipo()));
        for (SinistroStrategy strategy : strategiesList) {
            TipoSinistro tipo = strategy.getTipo();
            strategies.put(tipo, strategy);
        }
    }

    public SinistroStrategy getStrategy(TipoSinistro tipo) {
        return strategies.get(tipo);
    }
}