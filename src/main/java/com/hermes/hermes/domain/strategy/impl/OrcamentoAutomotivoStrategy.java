package com.hermes.hermes.domain.strategy.impl;

import com.hermes.hermes.domain.model.orcamento.ItemOrcamento;
import com.hermes.hermes.domain.model.orcamento.automotivo.MaoDeObra;
import com.hermes.hermes.domain.model.orcamento.automotivo.Peca;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
import com.hermes.hermes.domain.strategy.OrcamentoStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrcamentoAutomotivoStrategy implements OrcamentoStrategy {
    
    @Override
    public BigDecimal calcularCustos(Sinistro sinistro) {
        List<ItemOrcamento> itens = criarItensOrcamento(sinistro);
        return itens.stream()
                .map(ItemOrcamento::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public List<ItemOrcamento> criarItensOrcamento(Sinistro sinistro) {
        List<ItemOrcamento> itens = new ArrayList<>();
        
        // Adiciona peças básicas
        itens.addAll(criarPecasBasicas(sinistro));
        
        // Adiciona mão de obra básica
        itens.addAll(criarMaoDeObraBasica(sinistro));
        
        return itens;
    }
    
    @Override
    public boolean suportaTipo(String tipoSinistro) {
        return tipoSinistro != null && 
               (tipoSinistro.contains("AUTOMOTIVO") || 
                tipoSinistro.contains("COLISAO") ||
                tipoSinistro.contains("VEICULO"));
    }
    
    
    private List<Peca> criarPecasBasicas(Sinistro sinistro) {
        List<Peca> pecas = new ArrayList<>();
        
        // Peça exemplo
        Peca peca = new Peca();
        peca.setCodigo("PC-001");
        peca.setDescricao("Reparo de lataria");
        peca.setCategoria("Lataria");
        peca.setMarca("Original");
        peca.setValor(new BigDecimal("500.00"));
        peca.setQuantidade(1);
        pecas.add(peca);
        
        return pecas;
    }
    
    
    private List<MaoDeObra> criarMaoDeObraBasica(Sinistro sinistro) {
        List<MaoDeObra> servicos = new ArrayList<>();
        
        // Serviço exemplo
        MaoDeObra servico = new MaoDeObra();
        servico.setDescricao("Mão de obra de reparo");
        servico.setEspecialidade("Lataria");
        servico.setHorasEstimadas(4);
        servico.setValorHora(new BigDecimal("75.00"));
        servico.setDificuldadeServico("MEDIA");
        servico.setQuantidade(1);
        servicos.add(servico);
        
        return servicos;
    }
}