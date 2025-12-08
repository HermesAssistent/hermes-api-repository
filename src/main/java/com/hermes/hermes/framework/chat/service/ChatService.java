package com.hermes.hermes.framework.chat.service;
import com.hermes.hermes.framework.chat.domain.model.ChatMessage;
import com.hermes.hermes.framework.chat.domain.model.ChatSession;
import com.hermes.hermes.framework.cliente.domain.model.Cliente;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.exception.ExternalServiceException;
import com.hermes.hermes.framework.exception.InvalidResourceStateException;
import com.hermes.hermes.framework.exception.NotFoundException;
import com.hermes.hermes.framework.chat.repository.ChatMessageRepository;
import com.hermes.hermes.framework.chat.repository.ChatSessionRepository;
import com.hermes.hermes.framework.sinistro.repository.SinistroRepository;
import com.hermes.hermes.framework.cliente.service.ClienteService;
import com.hermes.hermes.framework.sinistro.service.SinistroService;
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
    private SinistroRepository sinistroRepository;
    private final FotoService fotoService;
    private final ClienteService clienteService;
    private final SinistroService sinistroService;

    @Autowired
    public ChatService(WebClient.Builder builder,
                       ChatSessionRepository sessionRepository,
                       ChatMessageRepository messageRepository,
                       ChatMessageRepository chatMessageRepository, SinistroRepository sinistroRepository, FotoService fotoService, ClienteService clienteService, SinistroService sinistroService) {
        this.webClient = builder.baseUrl("http://localhost:8000").build();
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.sinistroRepository = sinistroRepository;
        this.fotoService = fotoService;
        this.clienteService = clienteService;
        this.sinistroService = sinistroService;
    }

    public Map<String, Object> iniciarChat(Long userId) {
        Optional<ChatSession> existing = sessionRepository.findByUserIdAndAtivoIsTrue(userId);
        if (existing.isPresent()) {
            return Map.of(
                    "status", "existente",
                    "sessionId", existing.get().getId()
            );
        }
        // Cria sessão local
        ChatSession session = ChatSession.builder()
                .userId(userId)
                .messages(new ArrayList<>())
                .build();
        sessionRepository.save(session);

        // Chama API Python para iniciar sessão externa
        Map respostaExterna = webClient.post()
                .uri("/iniciar-chat")
                .bodyValue(Map.of("user_id", userId.toString()))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        // Retorna unindo info local + resposta externa
        return Map.of(
                "status", "nova",
                "sessionId", session.getId(),
                "respostaInicial", respostaExterna.get("resposta"),
                "conversa_finalizada", respostaExterna.get("conversa_finalizada")
        );
    }

    public Map<String, Object> processarMensagem(Long userId, String texto) {
        if (userId == null) {
            throw new InvalidResourceStateException("ID do usuário não fornecido");
        }
        if (texto == null || texto.trim().isEmpty()) {
            throw new InvalidResourceStateException("Mensagem não fornecida ou vazia");
        }

        // Recupera a sessão do banco
        ChatSession session = sessionRepository.findByUserIdAndAtivoIsTrue(userId)
                .orElseThrow(() -> new NotFoundException("Sessão não encontrada"));

        // Salva mensagem do usuário
        ChatMessage userMsg = ChatMessage.builder()
                .sender("USER")
                .content(texto)
                .timestamp(LocalDateTime.now())
                .session(session)
                .build();
        messageRepository.save(userMsg);

        // Chama serviço externo (sempre enviar user_id como String)
        Map respostaExterna;
        try {
            respostaExterna = webClient.post()
                    .uri("/processar-mensagem")
                    .bodyValue(Map.of("user_id", userId.toString(), "texto", texto))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new ExternalServiceException("ChatBot", e.getMessage());
        }

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

        if (Boolean.TRUE.equals(respostaExterna.get("conversa_finalizada"))) {
            Map<String, Object> resultado = (Map<String, Object>) respostaExterna.get("resultado");

            String tipoSinistro = (String) resultado.get("tipo");

            SinistroBase sinistro = sinistroService.criar(tipoSinistro, resultado);
            Cliente cliente = clienteService.findByUsuarioId(userId);
            sinistro.setCliente(cliente);
            sinistroRepository.save(sinistro);
            fotoService.relacionarSinistro(session, sinistro);
            this.limparSessao(userId);
        }

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
        if (userId == null) {
            throw new InvalidResourceStateException("ID do usuário não fornecido");
        }
       Optional<ChatSession> session = sessionRepository.findByUserIdAndAtivoIsTrue(userId);

       if (session.isPresent()) {
           session.get().setAtivo(false);
           sessionRepository.save(session.get());
       } else {
           log.error("Nenhuma sessão ativa encontrada para o usuário com ID: " + userId);
           throw new NotFoundException("Nenhuma sessão ativa encontrada para o usuário");
       }

        return Map.of("status", "sessão removida", "userId", userId);
    }

    public List<ChatMessage> listarMensagensDaSessao(Long sessionId) {
        if (sessionId == null) {
            throw new InvalidResourceStateException("ID da sessão não fornecido");
        }
        return chatMessageRepository.findBySessionIdIsOrderByTimestampAsc(sessionId);

    }
}
