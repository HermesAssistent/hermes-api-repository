package com.hermes.hermes.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.hermes.hermes.domain.model.abstracts.Usuario;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.ClienteRepository;
import com.hermes.hermes.repository.SeguradoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final ClienteRepository clienteRepository;
    private final SeguradoraRepository seguradoraRepository;

    public Usuario findByUid(String uid) {
        return clienteRepository.findByUid(uid).map(c -> (Usuario) c)
                .or(() -> seguradoraRepository.findByUid(uid).map(s -> (Usuario) s))
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado no sistema"));
    }

    public Cliente registrarCliente(String email, String senha, String nome, String cpf, String veiculo) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(senha);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        Cliente cliente = new Cliente();
        cliente.setUid(userRecord.getUid());
        cliente.setLogin(email);
        cliente.setEmail(email);
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setVeiculo(veiculo);

        return clienteRepository.save(cliente);
    }
}

