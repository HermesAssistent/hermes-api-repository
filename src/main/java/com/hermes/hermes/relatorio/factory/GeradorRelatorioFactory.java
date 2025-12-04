package com.hermes.hermes.relatorio.factory;

import com.hermes.hermes.relatorio.interfaces.GeradorRelatorioStrategy;
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
    public GeradorRelatorioStrategy obterStrategy(String tipoSinistro) {
        return strategies.stream()
                .filter(strategy -> strategy.suporta(tipoSinistro))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de sinistro não suportado: " + tipoSinistro +
                                ". Tipos válidos: automotivo, residencial, carga"
                ));
    }
}
