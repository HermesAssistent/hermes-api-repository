package com.hermes.hermes.service.chat;

import com.hermes.hermes.domain.model.chat.ChatSession;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository repository;

    public ChatSession buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sessão de chat não encontrada."));
    }
}
