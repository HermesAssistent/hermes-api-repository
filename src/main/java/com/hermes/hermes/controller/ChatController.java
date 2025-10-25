package com.hermes.hermes.controller;

import com.hermes.hermes.domain.model.chat.ChatMessage;
import com.hermes.hermes.service.chat.ChatService;
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
    public Map iniciar(@RequestParam Long userId) {
        return chatService.iniciarChat(userId);
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
