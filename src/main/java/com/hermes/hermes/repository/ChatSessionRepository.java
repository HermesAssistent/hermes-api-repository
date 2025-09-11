package com.hermes.hermes.repository;

import com.hermes.hermes.domain.model.chat.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findByUserId(Long userId);
}
