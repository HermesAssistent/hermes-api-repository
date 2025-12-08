package com.hermes.hermes.instancias.transporte.strategy;

import com.hermes.hermes.instancias.transporte.domain.enums.TipoAvaliacao;
import com.hermes.hermes.instancias.transporte.domain.enums.TipoCustoLogistico;
import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.instancias.transporte.domain.model.CustoLogistico;
import com.hermes.hermes.instancias.transporte.domain.model.CustoPericial;
import com.hermes.hermes.instancias.transporte.domain.model.CustoReposicao;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.orcamento.domain.strategy.OrcamentoStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrcamentoTransporteStrategy implements OrcamentoStrategy {
    
    @Override
    public BigDecimal calcularCustos(SinistroBase sinistro) {
        List<ItemOrcamento> itens = criarItensOrcamento(sinistro);
        return itens.stream()
                .map(ItemOrcamento::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public List<ItemOrcamento> criarItensOrcamento(SinistroBase sinistro) {
        List<ItemOrcamento> itens = new ArrayList<>();
        
        // Custo pericial obrigatório
        itens.add(criarCustoPericial(sinistro));
        
        // Reposição conforme necessário
        if (temAvariaCarga(sinistro)) {
            itens.add(criarCustoReposicao(sinistro));
        }
        
        // Custos logísticos adicionais
        itens.addAll(criarCustosLogisticos(sinistro));
        
        return itens;
    }
    
    @Override
    public boolean suportaTipo(String tipoSinistro) {
        return tipoSinistro != null && 
               (tipoSinistro.contains("TRANSPORTE") || 
                tipoSinistro.contains("CARGA") ||
                tipoSinistro.contains("AVARIA") ||
                tipoSinistro.contains("EXTRAVIO"));
    }
    
    
    private boolean temAvariaCarga(SinistroBase sinistro) {
        String problema = sinistro.getProblema() != null ? sinistro.getProblema().toLowerCase() : "";
        return problema.contains("avaria") || problema.contains("perda") || problema.contains("dano");
    }
    
    
    private CustoPericial criarCustoPericial(SinistroBase sinistro) {
        CustoPericial custo = new CustoPericial();
        custo.setDescricao("Perícia de transporte");
        custo.setTipoAvaliacao(TipoAvaliacao.COMPLETA);
        custo.setPeritoResponsavel("Perito Certificado");
        custo.setTempoEstimadoDias(3);
        custo.setValor(new BigDecimal("1200.00"));
        custo.setQuantidade(1);
        custo.setValorDeslocamento(new BigDecimal("200.00"));
        return custo;
    }
    
    
    private CustoReposicao criarCustoReposicao(SinistroBase sinistro) {
        CustoReposicao custo = new CustoReposicao();
        custo.setDescricao("Reposição de carga avariada");
        custo.setValorCarga(new BigDecimal("50000.00")); // Valor exemplo
        custo.setPercentualPerda(new BigDecimal("30.00")); // 30% de perda
        custo.setTipoCarga("Geral");
        custo.setValor(new BigDecimal("15000.00")); // 30% de 50k
        custo.setQuantidade(1);
        return custo;
    }
    
    
    private List<CustoLogistico> criarCustosLogisticos(SinistroBase sinistro) {
        List<CustoLogistico> custos = new ArrayList<>();
        
        // Custo de armazenagem
        CustoLogistico armazenagem = new CustoLogistico();
        armazenagem.setDescricao("Armazenagem temporária");
        armazenagem.setTipo(TipoCustoLogistico.ARMAZENAGEM);
        armazenagem.setUnidadeMedida("dias");
        armazenagem.setPeriodoDias(15);
        armazenagem.setTaxaDiaria(new BigDecimal("150.00"));
        armazenagem.setValor(new BigDecimal("2250.00")); // 15 * 150
        armazenagem.setQuantidade(1);
        custos.add(armazenagem);
        
        // Custo de transbordo se necessário
        if (temTransbordo(sinistro)) {
            CustoLogistico transbordo = new CustoLogistico();
            transbordo.setDescricao("Transbordo de carga");
            transbordo.setTipo(TipoCustoLogistico.TRANSBORDO);
            transbordo.setUnidadeMedida("operação");
            transbordo.setValor(new BigDecimal("800.00"));
            transbordo.setQuantidade(1);
            custos.add(transbordo);
        }
        
        return custos;
    }
    
    
    private boolean temTransbordo(SinistroBase sinistro) {
        String problema = sinistro.getProblema() != null ? sinistro.getProblema().toLowerCase() : "";
        return problema.contains("transbordo") || problema.contains("transferencia") || 
               problema.contains("acidente");
    }
}