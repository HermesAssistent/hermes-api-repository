package com.hermes.hermes.controller;

import com.google.firebase.auth.FirebaseToken;
import com.hermes.hermes.controller.dto.ClienteRegistroRequestDto;
import com.hermes.hermes.controller.dto.ClienteResponseDto;
import com.hermes.hermes.controller.dto.SeguradoraRegistroRequestDto;
import com.hermes.hermes.controller.dto.SeguradoraResponseDto;
import com.hermes.hermes.domain.model.seguradora.Seguradora;
import com.hermes.hermes.domain.model.usuario.Usuario;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.service.chat.ChatService;
import com.hermes.hermes.service.cliente.ClienteRegistroService;
import com.hermes.hermes.service.cliente.ClienteService;
import com.hermes.hermes.service.seguradora.SeguradoraRegistroService;
import com.hermes.hermes.service.UsuarioService;
import com.hermes.hermes.service.auth.FirebaseAuthService;
import com.hermes.hermes.service.seguradora.SeguradoraService;
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
