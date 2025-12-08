package com.hermes.hermes.framework.chat.repository;

import com.hermes.hermes.framework.chat.domain.model.ChatSession;
import com.hermes.hermes.framework.chat.domain.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FotoRepository extends JpaRepository<Foto, Long> {
    List<Foto> findAllByChatSession(ChatSession session);
}
