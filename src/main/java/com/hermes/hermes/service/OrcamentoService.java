package com.hermes.hermes.service;

import com.hermes.hermes.domain.enums.StatusOrcamento;
import com.hermes.hermes.domain.model.orcamento.ItemOrcamento;
import com.hermes.hermes.domain.model.orcamento.Orcamento;
import com.hermes.hermes.domain.model.oficina.Oficina;
import com.hermes.hermes.domain.model.sinistro.SinistroBase;
import com.hermes.hermes.domain.strategy.OrcamentoStrategy;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.OrcamentoRepository;
import com.hermes.hermes.repository.OficinaRepository;
import com.hermes.hermes.service.sinistro.SinistroService;
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
    private List<OrcamentoStrategy> strategies;

    @Autowired
    private SinistroService sinistroService;

    /**
     * Salva um orçamento no sistema.
     */
    public Orcamento salvar(Orcamento orcamento, Long sinistroId, Long prestadorId, String tipoSinistro) {
        // Busca o sinistro
        SinistroBase sinistro = sinistroService.buscarPorId(sinistroId, tipoSinistro);
        orcamento.setSinistro(sinistro);

        // Busca o prestador se informado
        if (prestadorId != null) {
            Oficina prestador = oficinaRepository.findById(prestadorId)
                    .orElseThrow(() -> new NotFoundException("Prestador não encontrado"));
            orcamento.setPrestador(prestador);
        }

        // Se não há itens, gera automaticamente usando strategy
        if (orcamento.getItens() == null || orcamento.getItens().isEmpty()) {
            List<ItemOrcamento> itensGerados = criarItensOrcamento(sinistro);
            for (ItemOrcamento item : itensGerados) {
                orcamento.adicionarItem(item);
            }
        } else {
            // Associa itens existentes ao orçamento
            for (ItemOrcamento item : orcamento.getItens()) {
                item.setOrcamento(orcamento);
            }
        }

        // Calcula valor total
        orcamento.calcularTotal();

        return orcamentoRepository.save(orcamento);
    }

    /**
     * Lista orçamentos por sinistro.
     */
    public List<Orcamento> listarPorSinistro(Long sinistroId) {
        return orcamentoRepository.findBySinistroId(sinistroId);
    }

    /**
     * Lista orçamentos por prestador.
     */
    public List<Orcamento> listarPorPrestador(Long prestadorId) {
        return orcamentoRepository.findByPrestadorId(prestadorId);
    }

    /**
     * Lista todos os orçamentos.
     */
    public List<Orcamento> listarTodos() {
        return orcamentoRepository.findAll();
    }

    /**
     * Lista orçamentos por cliente.
     */
    public List<Orcamento> listarPorCliente(Long clienteId) {
        return orcamentoRepository.findBySinistroClienteId(clienteId);
    }

    /**
     * Aceita um orçamento.
     */
    public Orcamento aceitar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.aceitar();
        return orcamentoRepository.save(orcamento);
    }

    /**
     * Revisa um orçamento com observações.
     */
    public Orcamento revisar(Long id, String observacoes) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.revisar(observacoes);
        return orcamentoRepository.save(orcamento);
    }

    /**
     * Rejeita um orçamento.
     */
    public Orcamento rejeitar(Long id, String motivo) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.rejeitar(motivo);
        return orcamentoRepository.save(orcamento);
    }

    /**
     * Calcula valor total de um orçamento.
     */
    public BigDecimal calcularValorTotal(Orcamento orcamento) {
        return orcamento.calcularTotal();
    }

    /**
     * Atualiza um orçamento existente.
     */
    public Orcamento atualizar(Long id, Orcamento dadosAtualizacao, Long sinistroId, Long prestadorId) {
        Orcamento orcamentoExistente = buscarPorId(id);

        // Atualiza observações
        if (dadosAtualizacao.getObservacoes() != null) {
            orcamentoExistente.setObservacoes(dadosAtualizacao.getObservacoes());
        }

        // Atualiza prestador se informado
        if (prestadorId != null) {
            Oficina prestador = oficinaRepository.findById(prestadorId)
                    .orElseThrow(() -> new NotFoundException("Prestador não encontrado"));
            orcamentoExistente.setPrestador(prestador);
        }

        // Recalcula total
        orcamentoExistente.calcularTotal();

        return orcamentoRepository.save(orcamentoExistente);
    }

    /**
     * Deleta um orçamento.
     */
    public void deletar(Long id) {
        Orcamento orcamento = buscarPorId(id);
        orcamentoRepository.delete(orcamento);
    }

    /**
     * Lista orçamentos por status.
     */
    public List<Orcamento> listarPorStatus(StatusOrcamento status) {
        return orcamentoRepository.findByStatus(status);
    }

    /**
     * Busca orçamento por ID.
     */
    private Orcamento buscarPorId(Long id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orçamento não encontrado"));
    }

    /**
     * Cria itens de orçamento usando Strategy pattern.
     */
    private List<ItemOrcamento> criarItensOrcamento(SinistroBase sinistro) {
        String tipoSinistro = determinarTipoSinistro(sinistro);

        OrcamentoStrategy strategy = strategies.stream()
                .filter(s -> s.suportaTipo(tipoSinistro))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Estratégia não encontrada para tipo: " + tipoSinistro));

        return strategy.criarItensOrcamento(sinistro);
    }

    /**
     * Determina o tipo de sinistro baseado nas informações disponíveis.
     */
    private String determinarTipoSinistro(SinistroBase sinistro) {
        // Lógica para determinar tipo baseado no sinistro
        String problema = sinistro.getProblema() != null ? sinistro.getProblema().toLowerCase() : "";
        String categoria = sinistro.getCategoriaProblema() != null ? sinistro.getCategoriaProblema().toLowerCase() : "";

        if (problema.contains("veiculo") || problema.contains("colisao") || categoria.contains("automotivo")) {
            return "AUTOMOTIVO";
        } else if (problema.contains("casa") || problema.contains("vazamento") || categoria.contains("domestico")) {
            return "DOMESTICO";
        } else if (problema.contains("carga") || problema.contains("transporte") || categoria.contains("transporte")) {
            return "TRANSPORTE";
        }

        // Default para automotivo se não conseguir determinar
        return "AUTOMOTIVO";
    }

    /**
     * Métodos para compatibilidade com estrutura anterior
     */
    @Deprecated
    public List<Orcamento> listarPorOficina(Long oficinaId) {
        return listarPorPrestador(oficinaId);
    }
}
