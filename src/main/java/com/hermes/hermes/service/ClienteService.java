package com.hermes.hermes.service;

import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.exception.DuplicateResourceException;
import com.hermes.hermes.exception.InvalidResourceStateException;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.ClienteRepository;
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
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        log.info("Buscando todos os clientes ativos");
        return clienteRepository.findAllActives();
    }

    public Cliente findById(Long id) {
        log.info("Buscando cliente com ID: {}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado com ID: " + id));
    }

    public Cliente create(Cliente cliente) {
        log.info("Criando novo cliente: {}", cliente.getUsuario().getNome());
        if (cliente.getCpf() != null && clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new DuplicateResourceException("CPF já cadastrado: " + cliente.getCpf());
        }
        if (cliente.getUsuario() == null || cliente.getCpf() == null) {
            throw new InvalidResourceStateException("Dados obrigatórios do cliente (usuário ou CPF) não fornecidos");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente clienteAtualizado) {
        Cliente cliente = findById(id);
        log.info("Atualizando cliente com ID: {}", id);
        if (clienteAtualizado.getUsuario() == null || clienteAtualizado.getCpf() == null) {
            throw new InvalidResourceStateException("Dados obrigatórios do cliente (usuário ou CPF) não fornecidos para atualização");
        }
        if (!Objects.equals(cliente.getCpf(), clienteAtualizado.getCpf()) &&
                clienteRepository.existsByCpf(clienteAtualizado.getCpf())) {
            throw new DuplicateResourceException("CPF já cadastrado: " + clienteAtualizado.getCpf());
        }
        cliente = clienteAtualizado;
        return clienteRepository.saveAndFlush(cliente);
    }

    public void delete(Long id) {
        Cliente cliente = findById(id);
        log.info("Deletando cliente com ID: {}", id);
        cliente.getUsuario().setAtivo(false);
        clienteRepository.save(cliente);
    }
}
