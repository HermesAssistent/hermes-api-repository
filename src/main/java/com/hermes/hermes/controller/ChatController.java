package com.hermes.hermes.controller;

import com.hermes.hermes.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/iniciar")
    public Map iniciar(@RequestParam String userId) {
        return chatService.iniciarChat(userId);
    }

    @PostMapping("/processar")
    public Map processar(
            @RequestParam String userId,
            @RequestParam String texto
    ) {
        return chatService.processarMensagem(userId, texto);
    }

    @GetMapping("/sessoes")
    public Map listarSessoes() {
        return chatService.listarSessoes();
    }

    @DeleteMapping("/limpar/{userId}")
    public Map limpar(@PathVariable String userId) {
        return chatService.limparSessao(userId);
    }

}
