package com.hermes.hermes.framework.orcamento.domain.strategy;

import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;

import java.math.BigDecimal;
import java.util.List;

public abstract class AbstractOrcamentoStrategy implements OrcamentoStrategy {

    @Override
    public BigDecimal calcularCustos(SinistroBase sinistro) {
        List<ItemOrcamento> itens = criarItensOrcamento(sinistro);
        
        BigDecimal custoBase = itens.stream()
                .filter(item -> item != null && item.isValido())
                .map(ItemOrcamento::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return aplicarAjustes(custoBase, sinistro);
    }

    protected BigDecimal aplicarAjustes(BigDecimal custoBase, SinistroBase sinistro) {
        return custoBase;
    }

    protected String extrairProblema(SinistroBase sinistro) {
        return sinistro.getProblema() != null ? 
               sinistro.getProblema().toLowerCase() : "";
    }

    protected String extrairCategoria(SinistroBase sinistro) {
        return sinistro.getCategoriaProblema() != null ? 
               sinistro.getCategoriaProblema().toLowerCase() : "";
    }

    protected boolean contemPalavras(String texto, String... palavras) {
        for (String palavra : palavras) {
            if (texto.contains(palavra.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean suportaTipo(String tipoSinistro) {
        if (tipoSinistro == null) {
            return false;
        }
        return contemPalavras(tipoSinistro.toLowerCase(), getPalavrasChave());
    }
    
    protected abstract String[] getPalavrasChave();
}
