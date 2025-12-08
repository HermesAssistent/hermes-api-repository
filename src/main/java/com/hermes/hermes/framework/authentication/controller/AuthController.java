package com.hermes.hermes.framework.authentication.controller;

import com.google.firebase.auth.FirebaseToken;
import com.hermes.hermes.framework.cliente.domain.dtos.ClienteRegistroRequestDto;
import com.hermes.hermes.framework.cliente.domain.dtos.ClienteResponseDto;
import com.hermes.hermes.framework.seguradora.domain.dtos.SeguradoraRegistroRequestDto;
import com.hermes.hermes.framework.seguradora.domain.dtos.SeguradoraResponseDto;
import com.hermes.hermes.framework.seguradora.domain.model.Seguradora;
import com.hermes.hermes.framework.usuario.domain.model.Usuario;
import com.hermes.hermes.framework.cliente.domain.model.Cliente;
import com.hermes.hermes.framework.chat.service.ChatService;
import com.hermes.hermes.framework.cliente.service.ClienteRegistroService;
import com.hermes.hermes.framework.cliente.service.ClienteService;
import com.hermes.hermes.framework.seguradora.service.SeguradoraRegistroService;
import com.hermes.hermes.framework.usuario.service.UsuarioService;
import com.hermes.hermes.framework.authentication.service.FirebaseAuthService;
import com.hermes.hermes.framework.seguradora.service.SeguradoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ClienteRegistroService clienteRegistroService;
    private final ClienteService clienteService;
    private final SeguradoraService seguradoraService;
    private final SeguradoraRegistroService seguradoraRegistroService;
    private final UsuarioService usuarioService;
    private final FirebaseAuthService firebaseAuthService;
    private final ChatService chatService;

    @PostMapping("/registrar/cliente")
    public ResponseEntity<ClienteResponseDto> registrarCliente(@RequestBody ClienteRegistroRequestDto req) throws Exception {
        Cliente cliente = clienteRegistroService.registrarCliente(req);
        ClienteResponseDto response = new ClienteResponseDto(ClienteResponseDto.ClienteDto.from(cliente), null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrar/seguradora")
    public ResponseEntity<SeguradoraResponseDto> registrarSeguradora(@RequestBody SeguradoraRegistroRequestDto req) throws Exception {
        Seguradora seguradora = seguradoraRegistroService.registrarSeguradora(req);
        SeguradoraResponseDto response = new SeguradoraResponseDto(seguradora, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        FirebaseToken decoded = firebaseAuthService.verifyIdToken(token);
        Usuario u = usuarioService.findByUid(decoded.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", u);
        map.put("token", token);

        if (u.getRole().equals("CLIENTE")) {
            Cliente c = clienteService.findByUsuarioId(u.getId());
            map.put("clienteId", c.getId());
        } else {
            Seguradora s = seguradoraService.findByUsuarioId(u.getId());
            map.put("seguradoraId", s.getId());
        }

        return ResponseEntity.ok(map);
    }

    @PostMapping("/logout/{id}")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader, @PathVariable String id) throws Exception {
        String idToken = authHeader.replace("Bearer ", "");
        chatService.limparSessao(Long.parseLong(id));
        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(idToken);
        firebaseAuthService.revokeRefreshTokens(decodedToken.getUid());
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}
