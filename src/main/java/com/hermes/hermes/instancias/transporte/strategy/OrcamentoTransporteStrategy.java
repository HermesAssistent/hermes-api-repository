package com.hermes.hermes.instancias.transporte.strategy;

import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.orcamento.domain.strategy.AbstractOrcamentoStrategy;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrcamentoTransporteStrategy extends AbstractOrcamentoStrategy {
    
    @Override
    public List<ItemOrcamento> criarItensOrcamento(SinistroBase sinistro) {
        return new ArrayList<>();
    }
    
    @Override
    protected String[] getPalavrasChave() {
        return new String[]{"transporte", "carga", "avaria", "extravio", "logistica"};
    }
}