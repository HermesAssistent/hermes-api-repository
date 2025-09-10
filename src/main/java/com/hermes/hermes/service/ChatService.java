package com.hermes.hermes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class ChatService {
    private final WebClient webClient;

    public ChatService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8000").build();
    }

    public Map iniciarChat(String userId) {
        return webClient.post()
                .uri("/iniciar-chat")
                .bodyValue(Map.of("user_id", userId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }


    public Map processarMensagem(String userId, String texto) {
        return webClient.post()
                .uri("/processar-mensagem")
                .bodyValue(Map.of(
                        "user_id", userId,
                        "texto", texto
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map listarSessoes() {
        return webClient.get()
                .uri("/sessoes-ativas")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map limparSessao(String userId) {
        return webClient.delete()
                .uri("/limpar-sessao/{userId}", userId)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
