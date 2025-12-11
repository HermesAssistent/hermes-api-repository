package com.hermes.hermes.framework.orcamento.service;

import com.hermes.hermes.framework.orcamento.domain.enums.StatusOrcamento;
import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.orcamento.domain.model.Orcamento;
import com.hermes.hermes.framework.orcamento.domain.strategy.OrcamentoStrategy;
import com.hermes.hermes.instancias.automotivo.domain.model.Oficina;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.exception.NotFoundException;
import com.hermes.hermes.framework.orcamento.repository.OrcamentoRepository;
import com.hermes.hermes.instancias.automotivo.repository.OficinaRepository;
import com.hermes.hermes.framework.sinistro.service.SinistroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@Slf4j
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private SinistroService sinistroService;

    @Autowired
    private List<OrcamentoStrategy> orcamentoStrategies;
    
    @Autowired
    public void setOrcamentoStrategies(List<OrcamentoStrategy> strategies) {
        this.orcamentoStrategies = strategies;
        log.info("=== Strategies de Orçamento carregadas: {} ===", strategies.size());
        strategies.forEach(s -> log.info("- {}", s.getClass().getSimpleName()));
    }


    public Orcamento salvar(Orcamento orcamento, Long sinistroId, Long prestadorId, String tipoSinistro) {
        SinistroBase sinistro = sinistroService.buscarPorId(sinistroId, tipoSinistro);
        orcamento.setSinistro(sinistro);

        if (prestadorId != null) {
            Oficina prestador = oficinaRepository.findById(prestadorId)
                    .orElseThrow(() -> new NotFoundException("Prestador não encontrado"));
            orcamento.setPrestador(prestador);
        }
        if (orcamento.getItens() != null && !orcamento.getItens().isEmpty()) {
            for (ItemOrcamento item : orcamento.getItens()) {
                item.setOrcamento(orcamento);
            }
        }
        orcamento.calcularTotal();

        return orcamentoRepository.save(orcamento);
    }

    public Orcamento gerarOrcamentoAutomatico(Long sinistroId, Long prestadorId, String tipoSinistro) {
        String tipoSinistroCompleto = mapearTipoSinistro(tipoSinistro);
        SinistroBase sinistro = sinistroService.buscarPorId(sinistroId, tipoSinistroCompleto);
        
        OrcamentoStrategy strategy = orcamentoStrategies.stream()
                .filter(s -> s.suportaTipo(tipoSinistro))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Strategy de orçamento não encontrada para tipo: " + tipoSinistro));
        
        List<ItemOrcamento> itensGerados = strategy.criarItensOrcamento(sinistro);
        
        Orcamento orcamento = new Orcamento();
        orcamento.setSinistro(sinistro);
        orcamento.setObservacoes("Orçamento gerado automaticamente");
        
        if (prestadorId != null) {
            Oficina prestador = oficinaRepository.findById(prestadorId)
                    .orElseThrow(() -> new NotFoundException("Prestador não encontrado"));
            orcamento.setPrestador(prestador);
        }
        
        if (itensGerados != null && !itensGerados.isEmpty()) {
            itensGerados.forEach(orcamento::adicionarItem);
        }
        
        return orcamentoRepository.save(orcamento);
    }

    public BigDecimal calcularCustosEstimados(Long sinistroId, String tipoSinistro) {
        String tipoSinistroCompleto = mapearTipoSinistro(tipoSinistro);
        SinistroBase sinistro = sinistroService.buscarPorId(sinistroId, tipoSinistroCompleto);
        
        OrcamentoStrategy strategy = orcamentoStrategies.stream()
                .filter(s -> s.suportaTipo(tipoSinistro))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Strategy de orçamento não encontrada para tipo: " + tipoSinistro));
        
        return strategy.calcularCustos(sinistro);
    }
    
    private String mapearTipoSinistro(String tipoOrcamento) {
        String tipo = tipoOrcamento.toLowerCase();
        if (tipo.contains("automotivo") || tipo.contains("colisao") || tipo.contains("veiculo") || tipo.contains("carro")) {
            return "sinistroAutomotivo";
        }
        if (tipo.contains("transporte") || tipo.contains("carga") || tipo.contains("avaria") || tipo.contains("logistica")) {
            return "sinistroTransporte";
        }
        if (tipo.contains("domestico") || tipo.contains("vazamento") || tipo.contains("residencial")) {
            return "sinistroResidencial";
        }
        return tipoOrcamento;
    }

    public List<Orcamento> listarPorSinistro(Long sinistroId) {
        return orcamentoRepository.findBySinistroId(sinistroId);
    }

    public List<Orcamento> listarPorCliente(Long clienteId) {
        return orcamentoRepository.findBySinistroClienteId(clienteId);
    }

    public List<Orcamento> listarTodos() {
        return orcamentoRepository.findAll();
    }

    public Orcamento aceitar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.aceitar();
        return orcamentoRepository.save(orcamento);
    }

    public Orcamento revisar(Long id, String observacoes) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.revisar(observacoes);
        return orcamentoRepository.save(orcamento);
    }

    public List<Orcamento> listarPorStatus(StatusOrcamento status) {
        return orcamentoRepository.findByStatus(status);
    }

    public Orcamento atualizarStatus(Long id, StatusOrcamento novoStatus) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.setStatus(novoStatus);
        return orcamentoRepository.save(orcamento);
    }

    public Orcamento buscarPorId(Long id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orçamento não encontrado"));
    }
    
    public Orcamento adicionarItem(Long orcamentoId, ItemOrcamento item) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        orcamento.adicionarItem(item);
        return orcamentoRepository.save(orcamento);
    }
    
    public Orcamento adicionarItensEmLote(Long orcamentoId, List<ItemOrcamento> itens) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        itens.forEach(orcamento::adicionarItem);
        return orcamentoRepository.save(orcamento);
    }
    
    public Orcamento removerItem(Long orcamentoId, Long itemId) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        orcamento.removerItem(itemId);
        return orcamentoRepository.save(orcamento);
    }
    
    public Orcamento atualizarItem(Long orcamentoId, Long itemId, ItemOrcamento itemAtualizado) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        
        orcamento.removerItem(itemId);
        itemAtualizado.setId(itemId);
        orcamento.adicionarItem(itemAtualizado);
        
        return orcamentoRepository.save(orcamento);
    }
}
