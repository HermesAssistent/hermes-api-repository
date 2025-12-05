package com.hermes.hermes.framework.chat.service;

import com.hermes.hermes.framework.chat.domain.model.ChatSession;
import com.hermes.hermes.framework.exception.NotFoundException;
import com.hermes.hermes.framework.chat.repository.ChatSessionRepository;
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
