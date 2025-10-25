package com.hermes.hermes.service.seguradora;
import com.hermes.hermes.domain.model.seguradora.Seguradora;
import com.hermes.hermes.exception.DuplicateResourceException;
import com.hermes.hermes.exception.InvalidResourceStateException;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.SeguradoraRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SeguradoraService {

    private final SeguradoraRepository seguradoraRepository;

    public List<Seguradora> findAll() {
        log.info("Buscando todos as seguradoras ativos");
        return seguradoraRepository.findAll();
    }

    public Seguradora findById(Long id) {
        log.info("Buscando cliente com ID: {}", id);
        return seguradoraRepository.findByIdAndAtivoIsTrue(id)
                .orElseThrow(() -> new NotFoundException("Seguradora não encontrada com ID: " + id));
    }

    public Seguradora findByUsuarioId(Long usuarioId) {
        log.info("Buscando seguradora com usuario ID: {}", usuarioId);
        return seguradoraRepository.findByAtivoIsTrueAndUsuario_Id(usuarioId)
                .orElseThrow(() -> new NotFoundException("Seguradora não encontrada com usuário ID: " + usuarioId));
    }

    public Seguradora create(Seguradora seguradora) {
        log.info("Criando nova seguradora: {}", seguradora.getUsuario().getNome());
        if (seguradora.getCnpj() != null && seguradoraRepository.findByCnpj(seguradora.getCnpj()).isPresent()) {
            throw new DuplicateResourceException("CNPJ já cadastrado: " + seguradora.getCnpj());
        }
        if (seguradora.getUsuario() == null || seguradora.getCnpj() == null) {
            throw new InvalidResourceStateException("Dados obrigatórios da seguradora (usuário ou CNPJ) não fornecidos");
        }
        return seguradoraRepository.save(seguradora);
    }

    public Seguradora update(Long id, Seguradora seguradoraAtualizada) {
        Seguradora seguradora = findById(id);
        log.info("Atualizando seguradora com ID: {}", id);
        if (seguradoraAtualizada.getUsuario() == null || seguradoraAtualizada.getCnpj() == null) {
            throw new InvalidResourceStateException("Dados obrigatórios da seguradora (usuário ou CNPJ) não fornecidos para atualização");
        }
        if (!Objects.equals(seguradora.getCnpj(), seguradoraAtualizada.getCnpj()) &&
                seguradoraRepository.findByCnpj(seguradoraAtualizada.getCnpj()).isPresent()) {
            throw new DuplicateResourceException("CNPJ já cadastrado: " + seguradoraAtualizada.getCnpj());
        }
        seguradora = seguradoraAtualizada;
        return seguradoraRepository.saveAndFlush(seguradora);
    }

    public void delete(Long id) {
        Seguradora seguradora = findById(id);
        log.info("Deletando seguradora com ID: {}", id);
        seguradora.getUsuario().setAtivo(false);
        seguradoraRepository.save(seguradora);
    }
}
