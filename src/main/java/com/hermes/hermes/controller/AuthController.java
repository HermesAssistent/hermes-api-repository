package com.hermes.hermes.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.hermes.hermes.controller.dto.ClienteRegistroRequestDto;
import com.hermes.hermes.controller.dto.ClienteResponseDto;
import com.hermes.hermes.domain.model.abstracts.Usuario;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/registrar/cliente")
    public ResponseEntity<ClienteResponseDto> registrarCliente(@RequestBody ClienteRegistroRequestDto req) throws Exception {
        Cliente cliente = usuarioService.registrarCliente(req.getEmail(), req.getSenha(), req.getNome(), req.getCpf(), req.getVeiculo());

        ClienteResponseDto response = new ClienteResponseDto(cliente, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);

        Usuario u = usuarioService.findByUid(decoded.getUid());

        return ResponseEntity.ok(u);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String idToken = authHeader.replace("Bearer ", "");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            FirebaseAuth.getInstance().revokeRefreshTokens(uid);

            return ResponseEntity.ok("Logout realizado com sucesso");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }
}
