package com.hermes.hermes.controller;

import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        List<Cliente> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.findById(id);
        return ResponseEntity.ok(cliente);
    }

    // A criação fica por conta do serviço de usuário
    /*@PostMapping
    public ResponseEntity<Cliente> criar(@RequestBody @Valid Cliente cliente) {
        Cliente novoCliente = clienteService.create(cliente);
        log.info("Cliente criado com sucesso: {}", novoCliente.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
    }*/

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody @Valid Cliente cliente) {
        Cliente clienteAtualizado = clienteService.update(id, cliente);
        log.info("Cliente atualizado com sucesso: {}", clienteAtualizado.getNome());
        return ResponseEntity.ok(clienteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.delete(id);
        log.info("Cliente deletado com sucesso. ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
