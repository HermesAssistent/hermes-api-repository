package com.hermes.hermes.service;

import com.hermes.hermes.controller.dto.ClienteRegistroRequestDto;
import com.hermes.hermes.domain.enums.Role;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.domain.model.usuario.Usuario;
import com.hermes.hermes.exception.DuplicateResourceException;
import com.hermes.hermes.exception.InvalidResourceStateException;
import com.hermes.hermes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteRegistroService {

    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final UsuarioRepository usuarioRepository;

    public Cliente registrarCliente(ClienteRegistroRequestDto req) {
        validateRequest(req);
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("E-mail já cadastrado: " + req.getEmail());
        }

        Usuario usuario = usuarioService.criarUsuario(
                req.getEmail(),
                req.getSenha(),
                req.getNome(),
                req.getEndereco(),
                req.getTelefone(),
                req.getCelular(),
                Role.CLIENTE
        );

        Cliente cliente = new Cliente();
        cliente.setCpf(req.getCpf());
        cliente.setVeiculo(req.getVeiculo());
        cliente.setUsuario(usuario);

        return clienteService.create(cliente);
    }

    private void validateRequest(ClienteRegistroRequestDto req) {
        if (req.getEmail() == null || req.getSenha() == null || req.getNome() == null || req.getCpf() == null) {
            throw new InvalidResourceStateException("Campos obrigatórios (e-mail, senha, nome, CPF) não fornecidos");
        }
    }
}