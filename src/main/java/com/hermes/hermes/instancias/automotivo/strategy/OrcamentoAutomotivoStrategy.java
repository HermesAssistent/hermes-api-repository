package com.hermes.hermes.instancias.automotivo.strategy;

import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.orcamento.domain.strategy.AbstractOrcamentoStrategy;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OrcamentoAutomotivoStrategy extends AbstractOrcamentoStrategy {

    @Override
    public List<ItemOrcamento> criarItensOrcamento(SinistroBase sinistro) {
        return new ArrayList<>();
    }
    
    @Override
    protected String[] getPalavrasChave() {
        return new String[]{"automotivo", "colisao", "veiculo", "carro"};
    }
    
    @Override
    public Map<String, Object> obterFormulario() {
        return Map.of(
            "tipoSinistro", "AUTOMOTIVO",
            "tiposItem", List.of("PECA", "MAO_DE_OBRA"),
            "campos", Map.of(
                "PECA", List.of(
                    Map.of("nome", "codigo", "tipo", "string", "obrigatorio", true, "descricao", "Código da peça"),
                    Map.of("nome", "descricao", "tipo", "string", "obrigatorio", true, "descricao", "Descrição da peça"),
                    Map.of("nome", "categoria", "tipo", "string", "obrigatorio", false, "descricao", "Categoria (ex: funilaria, motor)"),
                    Map.of("nome", "marca", "tipo", "string", "obrigatorio", false, "descricao", "Marca (original/paralela)"),
                    Map.of("nome", "valor", "tipo", "decimal", "obrigatorio", true, "descricao", "Valor unitário"),
                    Map.of("nome", "quantidade", "tipo", "number", "obrigatorio", true, "descricao", "Quantidade")
                ),
                "MAO_DE_OBRA", List.of(
                    Map.of("nome", "descricao", "tipo", "string", "obrigatorio", true, "descricao", "Descrição do serviço"),
                    Map.of("nome", "especialidade", "tipo", "string", "obrigatorio", false, "descricao", "Especialidade (funilaria, elétrica, etc)"),
                    Map.of("nome", "horasEstimadas", "tipo", "number", "obrigatorio", true, "descricao", "Horas estimadas"),
                    Map.of("nome", "valorHora", "tipo", "decimal", "obrigatorio", true, "descricao", "Valor por hora"),
                    Map.of("nome", "quantidade", "tipo", "number", "obrigatorio", true, "descricao", "Quantidade de serviços")
                )
            )
        );
    }
}