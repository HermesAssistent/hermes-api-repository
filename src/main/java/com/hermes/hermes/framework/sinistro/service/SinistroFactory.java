package com.hermes.hermes.framework.sinistro.service;

import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SinistroFactory {
    private final Map<String, SinistroStrategy> strategies = new HashMap<>();

    @Autowired
    public SinistroFactory(List<SinistroStrategy> strategiesList) {
        System.out.println("Strategies encontradas:");
        strategiesList.forEach(s -> System.out.println("- " + s.getClass().getSimpleName()));
        for (SinistroStrategy strategy : strategiesList) {
            strategies.put(strategy.getTipo(), strategy);
        }
    }

    public SinistroStrategy getStrategy(String tipo) {
        return strategies.get(tipo);
    }
}