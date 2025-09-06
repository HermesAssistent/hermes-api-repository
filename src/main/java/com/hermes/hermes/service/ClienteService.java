package com.hermes.hermes.service;

import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAllActives();
    }

    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado com ID: " + id));
    }

    public Cliente create(Cliente cliente) {
        log.info("Criando novo cliente: {}", cliente.getNome());
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente clienteAtualizado) {
        Cliente cliente = findById(id);
        cliente.setNome(clienteAtualizado.getNome());
        cliente.setVeiculo(clienteAtualizado.getVeiculo());
        log.info("Atualizando cliente com ID: {}", id);
        return clienteRepository.saveAndFlush(cliente);
    }

    public void delete(Long id) {
        Cliente cliente = findById(id);
        log.info("Deletando cliente com ID: {}", id);
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }
}
