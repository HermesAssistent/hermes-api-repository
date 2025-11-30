package com.hermes.hermes.domain.strategy;

import com.hermes.hermes.domain.model.orcamento.ItemOrcamento;
import com.hermes.hermes.domain.model.sinistro.Sinistro;

import java.math.BigDecimal;
import java.util.List;

public interface OrcamentoStrategy {
    
    BigDecimal calcularCustos(Sinistro sinistro);
    
    List<ItemOrcamento> criarItensOrcamento(Sinistro sinistro);
    
    boolean suportaTipo(String tipoSinistro);
}