package com.hermes.hermes.service.seguradora;

import com.hermes.hermes.controller.dto.SeguradoraRegistroRequestDto;
import com.hermes.hermes.domain.enums.Role;
import com.hermes.hermes.domain.model.seguradora.Seguradora;
import com.hermes.hermes.domain.model.usuario.Usuario;
import com.hermes.hermes.exception.DuplicateResourceException;
import com.hermes.hermes.exception.InvalidResourceStateException;
import com.hermes.hermes.repository.UsuarioRepository;
import com.hermes.hermes.service.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeguradoraRegistroService {

    private final UsuarioService usuarioService;
    private final SeguradoraService seguradoraService;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Seguradora registrarSeguradora(SeguradoraRegistroRequestDto req) {
        validateRequest(req);
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("E-mail já cadastrado: " + req.getEmail());
        }

        Usuario usuario = usuarioService.criarUsuario(
                req.getEmail(),
                req.getSenha(),
                req.getRazaoSocial(),
                req.getEndereco(),
                req.getTelefone(),
                req.getCelular(),
                Role.SEGURADORA
        );

        Seguradora seguradora = new Seguradora();
        seguradora.setRazaoSocial(req.getRazaoSocial());
        seguradora.setContato(req.getCelular());
        seguradora.setCnpj(req.getCnpj());
        seguradora.setUsuario(usuario);

        return seguradoraService.create(seguradora);
    }

    private void validateRequest(SeguradoraRegistroRequestDto req) {
        if (req.getEmail() == null || req.getSenha() == null || req.getRazaoSocial() == null || req.getCnpj() == null) {
            throw new InvalidResourceStateException("Campos obrigatórios (e-mail, senha, razão social, CNPJ) não fornecidos");
        }
    }
}