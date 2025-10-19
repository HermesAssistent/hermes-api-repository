package com.hermes.hermes.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.hermes.hermes.controller.dto.ClienteRegistroRequestDto;
import com.hermes.hermes.controller.dto.ClienteResponseDto;
import com.hermes.hermes.domain.model.usuario.Usuario;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.service.ClienteRegistroService;
import com.hermes.hermes.service.UsuarioService;
import com.hermes.hermes.service.auth.FirebaseAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ClienteRegistroService clienteRegistroService;
    private final UsuarioService usuarioService;
    private final FirebaseAuthService firebaseAuthService;

    @PostMapping("/registrar/cliente")
    public ResponseEntity<ClienteResponseDto> registrarCliente(@RequestBody ClienteRegistroRequestDto req) throws Exception {
        Cliente cliente = clienteRegistroService.registrarCliente(req);
        ClienteResponseDto response = new ClienteResponseDto(cliente, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        FirebaseToken decoded = firebaseAuthService.verifyIdToken(token);
        Usuario u = usuarioService.findByUid(decoded.getUid());
        return ResponseEntity.ok(u);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String idToken = authHeader.replace("Bearer ", "");
        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(idToken);
        firebaseAuthService.revokeRefreshTokens(decodedToken.getUid());
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}
