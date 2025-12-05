package com.hermes.hermes.framework.chat.repository;

import com.hermes.hermes.framework.chat.domain.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findByUserIdAndAtivoIsTrue(Long userId);
}
