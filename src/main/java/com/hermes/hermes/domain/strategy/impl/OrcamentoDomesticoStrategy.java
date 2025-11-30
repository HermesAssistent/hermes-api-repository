package com.hermes.hermes.domain.strategy.impl;

import com.hermes.hermes.domain.enums.NivelUrgencia;
import com.hermes.hermes.domain.model.orcamento.ItemOrcamento;
import com.hermes.hermes.domain.model.orcamento.domestico.Material;
import com.hermes.hermes.domain.model.orcamento.domestico.ServicoTecnico;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
import com.hermes.hermes.domain.strategy.OrcamentoStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrcamentoDomesticoStrategy implements OrcamentoStrategy {
    
    @Override
    public BigDecimal calcularCustos(Sinistro sinistro) {
        List<ItemOrcamento> itens = criarItensOrcamento(sinistro);
        
        BigDecimal custoBase = itens.stream()
                .map(ItemOrcamento::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Aplica multiplicador de urgência
        return aplicarUrgencia(custoBase, determinarUrgencia(sinistro));
    }
    
    @Override
    public List<ItemOrcamento> criarItensOrcamento(Sinistro sinistro) {
        List<ItemOrcamento> itens = new ArrayList<>();
        
        // Adiciona materiais básicos
        itens.addAll(criarMateriaisBasicos(sinistro));
        
        // Adiciona serviços técnicos
        itens.addAll(criarServicosTecnicos(sinistro));
        
        return itens;
    }
    
    @Override
    public boolean suportaTipo(String tipoSinistro) {
        return tipoSinistro != null && 
               (tipoSinistro.contains("DOMESTICO") || 
                tipoSinistro.contains("VAZAMENTO") ||
                tipoSinistro.contains("ELETRICO") ||
                tipoSinistro.contains("INFILTRACAO"));
    }
    
    
    private BigDecimal aplicarUrgencia(BigDecimal custoBase, NivelUrgencia urgencia) {
        BigDecimal multiplicador = switch (urgencia) {
            case EMERGENCIAL -> new BigDecimal("1.5"); // +50%
            case URGENTE -> new BigDecimal("1.3");     // +30%
            case NORMAL -> BigDecimal.ONE;             // sem acréscimo
        };
        
        return custoBase.multiply(multiplicador);
    }
    
    
    private NivelUrgencia determinarUrgencia(Sinistro sinistro) {
        String problema = sinistro.getProblema() != null ? sinistro.getProblema().toLowerCase() : "";
        
        if (problema.contains("emergencia") || problema.contains("urgente") || 
            problema.contains("vazamento grave") || problema.contains("incendio")) {
            return NivelUrgencia.EMERGENCIAL;
        } else if (problema.contains("vazamento") || problema.contains("eletrico")) {
            return NivelUrgencia.URGENTE;
        } else {
            return NivelUrgencia.NORMAL;
        }
    }
    
    
    private List<Material> criarMateriaisBasicos(Sinistro sinistro) {
        List<Material> materiais = new ArrayList<>();
        
        // Material exemplo
        Material material = new Material();
        material.setDescricao("Material hidráulico básico");
        material.setUnidadeMedida("metro");
        material.setCategoria("Hidráulica");
        material.setFornecedor("Local");
        material.setValor(new BigDecimal("25.00"));
        material.setQuantidade(5);
        material.setQuantidadeEstoque(100);
        materiais.add(material);
        
        return materiais;
    }
    
    
    private List<ServicoTecnico> criarServicosTecnicos(Sinistro sinistro) {
        List<ServicoTecnico> servicos = new ArrayList<>();
        
        // Serviço exemplo
        ServicoTecnico servico = new ServicoTecnico();
        servico.setDescricao("Reparo hidráulico");
        servico.setEspecialidade("Encanador");
        servico.setHorasEstimadas(3);
        servico.setValorHora(new BigDecimal("80.00"));
        servico.setComplexidadeServico("MEDIO");
        servico.setRequerCertificacao(false);
        servico.setQuantidade(1);
        servicos.add(servico);
        
        return servicos;
    }
}