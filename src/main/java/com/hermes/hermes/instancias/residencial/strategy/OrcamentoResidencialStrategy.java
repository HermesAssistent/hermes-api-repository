package com.hermes.hermes.instancias.residencial.strategy;

import com.hermes.hermes.instancias.residencial.domain.enums.NivelUrgencia;
import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.instancias.residencial.domain.model.Material;
import com.hermes.hermes.instancias.residencial.domain.model.ServicoTecnico;
import com.hermes.hermes.framework.orcamento.domain.strategy.AbstractOrcamentoStrategy;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    
    @Override
    public Map<String, Object> obterFormulario() {
        return Map.of(
            "tipoSinistro", "DOMESTICO",
            "tiposItem", List.of("MATERIAL", "SERVICO_TECNICO"),
            "campos", Map.of(
                "MATERIAL", List.of(
                    Map.of("nome", "descricao", "tipo", "string", "obrigatorio", true, "descricao", "Descrição do material"),
                    Map.of("nome", "unidadeMedida", "tipo", "string", "obrigatorio", true, "descricao", "Unidade (m², litros, kg, etc)"),
                    Map.of("nome", "categoria", "tipo", "string", "obrigatorio", false, "descricao", "Categoria do material"),
                    Map.of("nome", "fornecedor", "tipo", "string", "obrigatorio", false, "descricao", "Fornecedor"),
                    Map.of("nome", "valor", "tipo", "decimal", "obrigatorio", true, "descricao", "Valor unitário"),
                    Map.of("nome", "quantidade", "tipo", "number", "obrigatorio", true, "descricao", "Quantidade")
                ),
                "SERVICO_TECNICO", List.of(
                    Map.of("nome", "descricao", "tipo", "string", "obrigatorio", true, "descricao", "Descrição do serviço"),
                    Map.of("nome", "especialidade", "tipo", "string", "obrigatorio", false, "descricao", "Especialidade (encanador, eletricista, etc)"),
                    Map.of("nome", "horasEstimadas", "tipo", "number", "obrigatorio", true, "descricao", "Horas estimadas"),
                    Map.of("nome", "valorHora", "tipo", "decimal", "obrigatorio", true, "descricao", "Valor por hora"),
                    Map.of("nome", "quantidade", "tipo", "number", "obrigatorio", true, "descricao", "Quantidade de serviços")
                )
            )
        );
    }
}