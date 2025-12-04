package com.hermes.hermes.repository;
import com.hermes.hermes.domain.model.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = "select cm.* from chat_messages cm where cm.session_id = :sessionId order by cm.id", nativeQuery = true)
    List<ChatMessage> findBySessionIdIsOrderByTimestampAsc(Long sessionId);
}
