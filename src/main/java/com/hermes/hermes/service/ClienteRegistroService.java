package com.hermes.hermes.service;

import com.hermes.hermes.controller.dto.ClienteRegistroRequestDto;
import com.hermes.hermes.domain.enums.Role;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.domain.model.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteRegistroService {

    private final UsuarioService usuarioService;
    private final ClienteService clienteService;

    public Cliente registrarCliente(ClienteRegistroRequestDto req) throws Exception {
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
}