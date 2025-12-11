package com.hermes.hermes.framework.relatorio.service;

import com.hermes.hermes.framework.relatorio.domain.strategy.GeradorRelatorioStrategy;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory que seleciona a estratégia apropriada baseada no tipo de sinistro
 */
@Component
@RequiredArgsConstructor
public class GeradorRelatorioFactory {

    private final List<GeradorRelatorioStrategy> strategies;

    /**
     * Obtém a estratégia apropriada para o tipo de sinistro
     * @param tipoSinistro tipo do sinistro (automotivo, residencial, carga)
     * @return estratégia correspondente
     * @throws IllegalArgumentException se não houver estratégia para o tipo
     */
    public GeradorRelatorioStrategy obterStrategy(TipoSinistro tipoSinistro) {
        return strategies.stream()
                .filter(strategy -> strategy.suporta(tipoSinistro))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de sinistro não suportado: " + tipoSinistro +
                                ". Tipos válidos: automotivo, residencial, carga"
                ));
    }
}
