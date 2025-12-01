package com.hermes.hermes.domain.strategy;

import com.hermes.hermes.domain.model.orcamento.ItemOrcamento;
import com.hermes.hermes.domain.model.sinistro.SinistroBase;

import java.math.BigDecimal;
import java.util.List;

public interface OrcamentoStrategy {
    
    BigDecimal calcularCustos(SinistroBase sinistro);
    
    List<ItemOrcamento> criarItensOrcamento(SinistroBase sinistro);
    
    boolean suportaTipo(String tipoSinistro);
}