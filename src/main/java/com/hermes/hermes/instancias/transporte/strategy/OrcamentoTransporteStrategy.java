package com.hermes.hermes.instancias.transporte.strategy;

import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.orcamento.domain.strategy.AbstractOrcamentoStrategy;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    
    @Override
    public Map<String, Object> obterFormulario() {
        return Map.of(
            "tipoSinistro", "TRANSPORTE",
            "tiposItem", List.of("CUSTO_PERICIAL", "CUSTO_REPOSICAO", "CUSTO_LOGISTICO"),
            "campos", Map.of(
                "CUSTO_PERICIAL", List.of(
                    Map.of("nome", "descricao", "tipo", "string", "obrigatorio", true, "descricao", "Descrição da perícia"),
                    Map.of("nome", "tipoAvaliacao", "tipo", "enum", "obrigatorio", true, "descricao", "Tipo de avaliação", 
                           "opcoes", List.of("SIMPLES", "VISTORIA", "COMPLETA", "EMERGENCIAL")),
                    Map.of("nome", "valor", "tipo", "decimal", "obrigatorio", true, "descricao", "Valor do serviço"),
                    Map.of("nome", "quantidade", "tipo", "number", "obrigatorio", true, "descricao", "Quantidade")
                ),
                "CUSTO_REPOSICAO", List.of(
                    Map.of("nome", "descricao", "tipo", "string", "obrigatorio", true, "descricao", "Descrição da carga"),
                    Map.of("nome", "valorCarga", "tipo", "decimal", "obrigatorio", true, "descricao", "Valor da carga"),
                    Map.of("nome", "percentualPerda", "tipo", "decimal", "obrigatorio", true, "descricao", "Percentual de perda (0-100)"),
                    Map.of("nome", "valor", "tipo", "decimal", "obrigatorio", true, "descricao", "Valor de reposição"),
                    Map.of("nome", "quantidade", "tipo", "number", "obrigatorio", true, "descricao", "Quantidade")
                ),
                "CUSTO_LOGISTICO", List.of(
                    Map.of("nome", "descricao", "tipo", "string", "obrigatorio", true, "descricao", "Descrição do custo logístico"),
                    Map.of("nome", "tipoCustoLogistico", "tipo", "enum", "obrigatorio", true, "descricao", "Tipo de custo",
                           "opcoes", List.of("ARMAZENAGEM", "FRETE", "MANUSEIO", "SEGURO", "OUTROS")),
                    Map.of("nome", "unidadeMedida", "tipo", "string", "obrigatorio", false, "descricao", "Unidade (dias, m³, ton, etc)"),
                    Map.of("nome", "valor", "tipo", "decimal", "obrigatorio", true, "descricao", "Valor do custo"),
                    Map.of("nome", "quantidade", "tipo", "number", "obrigatorio", true, "descricao", "Quantidade")
                )
            )
        );
    }
}