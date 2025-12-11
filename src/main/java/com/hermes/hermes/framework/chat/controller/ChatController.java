package com.hermes.hermes.framework.chat.controller;

import com.hermes.hermes.framework.chat.domain.model.ChatMessage;
import com.hermes.hermes.framework.chat.service.ChatService;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;


    @GetMapping("/listar-mensagens/{sessionId}")
    public List<ChatMessage> listarMensagens(@PathVariable("sessionId") String sessionId) {
        return chatService.listarMensagensDaSessao(Long.valueOf(sessionId));
    }

    @PostMapping("/iniciar")
    public Map iniciar(@RequestParam Long userId, @RequestParam String tipoSinistro) {
        return chatService.iniciarChat(userId, TipoSinistro.fromString(tipoSinistro));
    }

    @PostMapping("/processar")
    public Map processar(
            @RequestParam Long userId,
            @RequestParam String texto
    ) {
        return chatService.processarMensagem(userId, texto);
    }

    @GetMapping("/sessoes")
    public Map listarSessoes() {
        return chatService.listarSessoes();
    }

    @DeleteMapping("/limpar/{userId}")
    public Map limpar(@PathVariable Long userId) {
        return chatService.limparSessao(userId);
    }


}
