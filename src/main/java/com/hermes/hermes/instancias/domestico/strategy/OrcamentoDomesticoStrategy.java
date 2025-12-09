package com.hermes.hermes.instancias.domestico.strategy;

import com.hermes.hermes.instancias.domestico.domain.enums.NivelUrgencia;
import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.orcamento.domain.strategy.AbstractOrcamentoStrategy;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrcamentoDomesticoStrategy extends AbstractOrcamentoStrategy {
    
    @Override
    public List<ItemOrcamento> criarItensOrcamento(SinistroBase sinistro) {
        return new ArrayList<>();
    }
    
    @Override
    protected BigDecimal aplicarAjustes(BigDecimal custoBase, SinistroBase sinistro) {
        NivelUrgencia urgencia = determinarUrgencia(sinistro);
        BigDecimal multiplicador = obterMultiplicadorUrgencia(urgencia);
        return custoBase.multiply(multiplicador);
    }
    
    @Override
    protected String[] getPalavrasChave() {
        return new String[]{"domestico", "vazamento", "eletrico", "infiltracao", "residencial"};
    }
    
    private NivelUrgencia determinarUrgencia(SinistroBase sinistro) {
        String problema = extrairProblema(sinistro);
        
        if (contemPalavras(problema, "emergencia", "urgente", "grave", "incendio")) {
            return NivelUrgencia.EMERGENCIAL;
        } else if (contemPalavras(problema, "vazamento", "eletrico")) {
            return NivelUrgencia.URGENTE;
        }
        return NivelUrgencia.NORMAL;
    }
    
    private BigDecimal obterMultiplicadorUrgencia(NivelUrgencia urgencia) {
        return switch (urgencia) {
            case EMERGENCIAL -> new BigDecimal("1.5"); // +50%
            case URGENTE -> new BigDecimal("1.3");     // +30%
            case NORMAL -> BigDecimal.ONE;             // sem acréscimo
        };
    }
}