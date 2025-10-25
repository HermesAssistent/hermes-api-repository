package com.hermes.hermes.controller;

import com.hermes.hermes.controller.dto.ClienteResponseDto;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.service.cliente.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<List<ClienteResponseDto.ClienteDto>> listar() {
        List<ClienteResponseDto.ClienteDto> clientes = clienteService.findAll().stream().map(ClienteResponseDto.ClienteDto::from).toList();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto.ClienteDto> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.findById(id);
        return ResponseEntity.ok(ClienteResponseDto.ClienteDto.from(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto.ClienteDto> atualizar(@PathVariable Long id, @RequestBody @Valid Cliente cliente) {
        Cliente clienteAtualizado = clienteService.update(id, cliente);
        log.info("Cliente atualizado com sucesso: {}", clienteAtualizado.getUsuario().getNome());
        return ResponseEntity.ok(ClienteResponseDto.ClienteDto.from(clienteAtualizado));
    }

    @PutMapping("/vincular-seguradora/{seguradoraId}/{clienteId}")
    public ResponseEntity<ClienteResponseDto.ClienteDto> vincularSeguradoraComCliente(@PathVariable("seguradoraId") Long seguradoraId, @PathVariable("clienteId") Long clienteId) {
        Cliente clienteAtualizado = clienteService.vincularClienteComSeguradora(seguradoraId, clienteId);
        log.info("Cliente atualizado com sucesso: {}", clienteAtualizado.getUsuario().getNome());
        return ResponseEntity.ok(ClienteResponseDto.ClienteDto.from(clienteAtualizado));
    }

    @PutMapping("/desvincular-seguradora/{clienteId}")
    public ResponseEntity<ClienteResponseDto.ClienteDto> vincularSeguradoraComCliente(@PathVariable("clienteId") Long clienteId) {
        Cliente clienteAtualizado = clienteService.desvincularClienteComSeguradora(clienteId);
        log.info("Cliente atualizado com sucesso: {}", clienteAtualizado.getUsuario().getNome());
        return ResponseEntity.ok(ClienteResponseDto.ClienteDto.from(clienteAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.delete(id);
        log.info("Cliente deletado com sucesso. ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
