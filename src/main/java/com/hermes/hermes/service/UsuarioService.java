package com.hermes.hermes.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.hermes.hermes.domain.enums.Role;
import com.hermes.hermes.domain.model.usuario.Usuario;
import com.hermes.hermes.exception.AuthenticationException;
import com.hermes.hermes.exception.ExternalServiceException;
import com.hermes.hermes.exception.InvalidResourceStateException;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.UsuarioRepository;
import com.hermes.hermes.service.auth.FirebaseAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final FirebaseAuthService firebaseAuthService;

    public Usuario findByUid(String uid) {
        if (uid == null || uid.isEmpty()) {
            throw new InvalidResourceStateException("UID não fornecido");
        }
        return usuarioRepository.findByUid(uid).orElseThrow(() -> new NotFoundException("Usuário não encontrado no sistema"));
    }

    @Transactional
    public Usuario criarUsuario(String email, String senha, String nome, String endereco, String telefone, String celular, Role role) {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(senha);

        UserRecord userRecord = firebaseAuthService.createUser(request);

        Usuario usuario = new Usuario();
        usuario.setUid(userRecord.getUid());
        usuario.setLogin(email);
        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setEndereco(endereco);
        usuario.setTelefone(telefone);
        usuario.setCelular(celular);
        usuario.setRole(role.getValor());

        return usuarioRepository.save(usuario);
    }
}

