package com.hermes.hermes.framework.orcamento.domain.strategy;

import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;

import java.math.BigDecimal;
import java.util.List;

public interface OrcamentoStrategy {
    
    BigDecimal calcularCustos(SinistroBase sinistro);
    
    List<ItemOrcamento> criarItensOrcamento(SinistroBase sinistro);
    
    boolean suportaTipo(String tipoSinistro);
}