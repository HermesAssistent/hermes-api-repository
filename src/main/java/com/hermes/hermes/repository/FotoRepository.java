package com.hermes.hermes.repository;

import com.hermes.hermes.domain.model.chat.ChatSession;
import com.hermes.hermes.domain.model.chat.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FotoRepository extends JpaRepository<Foto, Long> {
    List<Foto> findAllByChatSession(ChatSession session);
}
