package com.hermes.hermes.service;

import com.hermes.hermes.domain.model.oficina.Orcamento;
import com.hermes.hermes.domain.model.oficina.Peca;
import com.hermes.hermes.domain.model.oficina.Oficina;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.OrcamentoRepository;
import com.hermes.hermes.repository.OficinaRepository;
import com.hermes.hermes.repository.SinistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private SinistroRepository sinistroRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    public Orcamento salvar(Orcamento orcamento, Long sinistroId, Long oficinaId) {
        if (sinistroId != null) {
            Sinistro s = sinistroRepository.findById(sinistroId)
                    .orElseThrow(() -> new NotFoundException("Sinistro não encontrado"));
            orcamento.setSinistro(s);
        }

        if (oficinaId != null) {
            Oficina o = oficinaRepository.findById(oficinaId)
                    .orElseThrow(() -> new NotFoundException("Oficina não encontrada"));
            orcamento.setOficina(o);
        }

        if (orcamento.getPecas() != null) {
            for (Peca p : orcamento.getPecas()) {
                p.setOrcamento(orcamento);
            }
        }

        orcamento.atualizarValorPecasAPartirDasPecas();

        return orcamentoRepository.save(orcamento);
    }

    public List<Orcamento> listarPorSinistro(Long sinistroId) {
        return orcamentoRepository.findBySinistroId(sinistroId);
    }

    public List<Orcamento> listarPorOficina(Long oficinaId) {
        return orcamentoRepository.findByOficinaId(oficinaId);
    }

    public List<Orcamento> listarTodos() {
        return orcamentoRepository.findAll();
    }

    public List<Orcamento> listarPorCliente(Long clienteId) {
        return orcamentoRepository.findBySinistroClienteId(clienteId);
    }

    public Orcamento atualizar(Long id, Orcamento dados, Long sinistroId, Long oficinaId) {
        Orcamento existente = orcamentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orçamento não encontrado"));

        
        existente.setDescricao(dados.getDescricao());
        existente.setValorMaoDeObra(dados.getValorMaoDeObra());
        existente.setPrazo(dados.getPrazo());

        if (sinistroId != null) {
            Sinistro s = sinistroRepository.findById(sinistroId)
                    .orElseThrow(() -> new NotFoundException("Sinistro não encontrado"));
            existente.setSinistro(s);
        }

        if (oficinaId != null) {
            Oficina o = oficinaRepository.findById(oficinaId)
                    .orElseThrow(() -> new NotFoundException("Oficina não encontrada"));
            existente.setOficina(o);
        }

        if (dados.getPecas() != null) {
            java.util.Map<Long, Peca> mapaExistentes = new java.util.HashMap<>();
            for (Peca p : existente.getPecas()) {
                if (p.getId() != null) {
                    mapaExistentes.put(p.getId(), p);
                }
            }

            java.util.List<Peca> resultado = new java.util.ArrayList<>();

            for (Peca p : dados.getPecas()) {
                if (p.getId() != null) {
                    Peca encontrada = mapaExistentes.get(p.getId());
                    if (encontrada == null) {
                        throw new NotFoundException("Peça não encontrada no orçamento: id=" + p.getId());
                    }
                    encontrada.setNome(p.getNome());
                    encontrada.setValor(p.getValor());
                    encontrada.setOrcamento(existente);
                    resultado.add(encontrada);
                    mapaExistentes.remove(p.getId());
                } else {
                    p.setOrcamento(existente);
                    resultado.add(p);
                }
            }
            existente.getPecas().clear();
            existente.getPecas().addAll(resultado);
        }

        existente.atualizarValorPecasAPartirDasPecas();
        return orcamentoRepository.save(existente);
    }

    public void deletar(Long id) {
        Orcamento existente = orcamentoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Orçamento não encontrado"));
        orcamentoRepository.delete(existente);
    }
}
