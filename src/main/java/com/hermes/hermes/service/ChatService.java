package com.hermes.hermes.service;
import com.hermes.hermes.domain.model.chat.ChatMessage;
import com.hermes.hermes.domain.model.chat.ChatSession;
import com.hermes.hermes.repository.ChatMessageRepository;
import com.hermes.hermes.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final WebClient webClient;
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatService(WebClient.Builder builder,
                       ChatSessionRepository sessionRepository,
                       ChatMessageRepository messageRepository,
                       ChatMessageRepository chatMessageRepository) {
        this.webClient = builder.baseUrl("http://localhost:8000").build();
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public Map<String, Object> iniciarChat(Long userId) {
        Optional<ChatSession> existing = sessionRepository.findByUserId(userId);
        if (existing.isPresent()) {
            return Map.of(
                    "status", "existente",
                    "sessionId", existing.get().getId()
            );
        }

        // 1️⃣ Cria sessão local
        ChatSession session = ChatSession.builder()
                .userId(userId)
                .messages(new ArrayList<>())
                .build();
        sessionRepository.save(session);

        // 2️⃣ Chama API Python para iniciar sessão externa
        Map respostaExterna = webClient.post()
                .uri("/iniciar-chat")
                .bodyValue(Map.of("user_id", userId.toString()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // 3️⃣ Retorna unindo info local + resposta externa
        return Map.of(
                "status", "nova",
                "sessionId", session.getId(),
                "respostaInicial", respostaExterna.get("resposta"),
                "conversa_finalizada", respostaExterna.get("conversa_finalizada")
        );
    }

    public Map<String, Object> processarMensagem(Long userId, String texto) {
        // Recupera a sessão do banco
        ChatSession session = sessionRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));

        // Salva mensagem do usuário
        ChatMessage userMsg = ChatMessage.builder()
                .sender("USER")
                .content(texto)
                .timestamp(LocalDateTime.now())
                .session(session)
                .build();
        messageRepository.save(userMsg);

        // Chama serviço externo (sempre enviar user_id como String)
        Map respostaExterna = webClient.post()
                .uri("/processar-mensagem")
                .bodyValue(Map.of("user_id", userId.toString(), "texto", texto))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // Captura a resposta (se vier algo do bot)
        String respostaBot = respostaExterna != null && respostaExterna.containsKey("resposta")
                ? respostaExterna.get("resposta").toString()
                : respostaExterna != null && respostaExterna.containsKey("mensagem_final")
                ? respostaExterna.get("mensagem_final").toString()
                : "Sem resposta";

        // Salva resposta do BOT no banco
        ChatMessage botMsg = ChatMessage.builder()
                .sender("BOT")
                .content(respostaBot)
                .timestamp(LocalDateTime.now())
                .session(session)
                .build();
        messageRepository.save(botMsg);

        assert respostaExterna != null;
        return Map.of(
                "userMessage", userMsg.getContent(),
                "botMessage", botMsg.getContent(),
                "rawResponse", respostaExterna
        );
    }


    public Map<String, Object> listarSessoes() {
        return Map.of("sessoes", sessionRepository.findAll());
    }

    public Map<String, Object> limparSessao(Long userId) {
        sessionRepository.findByUserId(userId)
                .ifPresent(sessionRepository::delete);

        // também limpa no serviço externo
        Map respostaExterna = webClient.delete()
                .uri("/limpar-sessao/{userId}", String.valueOf(userId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return Map.of("status", "sessão removida", "userId", userId, "externo", respostaExterna);
    }

    public List<ChatMessage> listarMensagensDaSessao(Long sessionId) {
        return chatMessageRepository.findBySessionIdIsOrderByTimestampAsc(sessionId);
    }
}
