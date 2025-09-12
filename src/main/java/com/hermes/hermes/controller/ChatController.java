package com.hermes.hermes.controller;

import com.hermes.hermes.domain.model.chat.ChatMessage;
import com.hermes.hermes.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;


    @GetMapping("/listar-mensagens/{sessionId}")
    public List<ChatMessage> iniciar(@PathVariable("sessionId") String sessionId) {
        try {
            return chatService.listarMensagensDaSessao(Long.valueOf(sessionId));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao listar Mensagens de Sessao");
        }
    }

    @PostMapping("/iniciar")
    public Map iniciar(@RequestParam Long userId) {
        try {
            return chatService.iniciarChat(userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao iniciar chat");
        }
    }

    @PostMapping("/receber-relato")
    public void receberRelato(@RequestParam Object userId) {
        try {
            System.out.println("relato" + userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao iniciar chat");
        }
    }

    @PostMapping("/processar")
    public Map processar(
            @RequestParam Long userId,
            @RequestParam String texto
    ) {
        try {
            return chatService.processarMensagem(userId, texto);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar chat");
        }
    }

    @GetMapping("/sessoes")
    public Map listarSessoes() {
        try {
            return chatService.listarSessoes();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao listar chat");
        }
    }

    @DeleteMapping("/limpar/{userId}")
    public Map limpar(@PathVariable Long userId) {
        try {
            return chatService.limparSessao(userId);
        }  catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao limpar chat");
        }

    }


}
