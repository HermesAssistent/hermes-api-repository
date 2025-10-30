package com.hermes.hermes.service.chat;
import com.hermes.hermes.domain.model.chat.ChatMessage;
import com.hermes.hermes.domain.model.chat.ChatSession;
import com.hermes.hermes.domain.model.cliente.Cliente;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
import com.hermes.hermes.exception.ExternalServiceException;
import com.hermes.hermes.exception.InvalidResourceStateException;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.ChatMessageRepository;
import com.hermes.hermes.repository.ChatSessionRepository;
import com.hermes.hermes.repository.SinistroRepository;
import com.hermes.hermes.service.FotoService;
import com.hermes.hermes.service.GeocodingService;
import com.hermes.hermes.service.cliente.ClienteService;
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
    private final GeocodingService geocodingService;

    @Autowired
    public ChatService(WebClient.Builder builder,
                       ChatSessionRepository sessionRepository,
                       ChatMessageRepository messageRepository,
                       ChatMessageRepository chatMessageRepository, SinistroRepository sinistroRepository, FotoService fotoService, ClienteService clienteService, GeocodingService geocodingService) {
        this.webClient = builder.baseUrl("http://localhost:8000").build();
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.sinistroRepository = sinistroRepository;
        this.fotoService = fotoService;
        this.clienteService = clienteService;
        this.geocodingService = geocodingService;
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
          Sinistro sinistro = Sinistro.fromMap((LinkedHashMap<String, Object>) respostaExterna.get("resultado"),
                  geocodingService);
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
