package com.hermes.hermes.instancias.domestico.strategy;

import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.orcamento.domain.strategy.AbstractOrcamentoStrategy;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrcamentoDomesticoStrategy extends AbstractOrcamentoStrategy {
    
    @Override
    public List<ItemOrcamento> criarItensOrcamento(SinistroBase sinistro) {
        // Retorna lista vazia - itens serão adicionados manualmente
        return new ArrayList<>();
    }
    
    @Override
    protected String[] getPalavrasChave() {
        return new String[]{"domestico", "vazamento", "eletrico", "infiltracao", "residencial"};
    }
}