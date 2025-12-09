package com.hermes.hermes.framework.orcamento.service;

import com.hermes.hermes.framework.orcamento.domain.enums.StatusOrcamento;
import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.orcamento.domain.model.Orcamento;
import com.hermes.hermes.instancias.automotivo.domain.model.Oficina;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.exception.NotFoundException;
import com.hermes.hermes.framework.orcamento.repository.OrcamentoRepository;
import com.hermes.hermes.instancias.automotivo.repository.OficinaRepository;
import com.hermes.hermes.framework.sinistro.service.SinistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private SinistroService sinistroService;


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

    //========= GERENCIAMENTO DE ORÇAMENTOS ==========

    public List<Orcamento> listarPorSinistro(Long sinistroId) {
        return orcamentoRepository.findBySinistroId(sinistroId);
    }

    public List<Orcamento> listarPorPrestador(Long prestadorId) {
        return orcamentoRepository.findByPrestadorId(prestadorId);
    }

    public List<Orcamento> listarTodos() {
        return orcamentoRepository.findAll();
    }

    public List<Orcamento> listarPorCliente(Long clienteId) {
        return orcamentoRepository.findBySinistroClienteId(clienteId);
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

    public Orcamento rejeitar(Long id, String motivo) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.rejeitar(motivo);
        return orcamentoRepository.save(orcamento);
    }

    public BigDecimal calcularValorTotal(Orcamento orcamento) {
        return orcamento.calcularTotal();
    }

    public Orcamento atualizar(Long id, Orcamento dadosAtualizacao, Long sinistroId, Long prestadorId) {
        Orcamento orcamentoExistente = buscarPorId(id);
        if (dadosAtualizacao.getObservacoes() != null) {
            orcamentoExistente.setObservacoes(dadosAtualizacao.getObservacoes());
        }
        if (prestadorId != null) {
            Oficina prestador = oficinaRepository.findById(prestadorId)
                    .orElseThrow(() -> new NotFoundException("Prestador não encontrado"));
            orcamentoExistente.setPrestador(prestador);
        }
        orcamentoExistente.calcularTotal();
        return orcamentoRepository.save(orcamentoExistente);
    }

    public void deletar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        orcamentoRepository.delete(orcamento);
    }

    public List<Orcamento> listarPorStatus(StatusOrcamento status) {
        return orcamentoRepository.findByStatus(status);
    }

    public Orcamento buscarPorId(Long id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orçamento não encontrado"));
    }
    
    // ========== GERENCIAMENTO DE ITENS ==========
    
    public Orcamento adicionarItem(Long orcamentoId, ItemOrcamento item) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        orcamento.adicionarItem(item);
        return orcamentoRepository.save(orcamento);
    }
    
    public Orcamento adicionarItensEmLote(Long orcamentoId, List<ItemOrcamento> itens) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        
        for (ItemOrcamento item : itens) {
            orcamento.adicionarItem(item);
        }
        
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
