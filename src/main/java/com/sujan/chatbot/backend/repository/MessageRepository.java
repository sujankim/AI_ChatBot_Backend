package com.sujan.chatbot.backend.repository;

import com.sujan.chatbot.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    /**
     * Get all messages for a specific chat, oldest first.
     * SQL: SELECT * FROM messages WHERE chat_session_id = ? ORDER BY created_at ASC
     *
     * ASC = oldest first (chronological — how chat history works)
     */
    List<Message> findByChatSessionIdOrderByCreatedAtAsc(Long chatSessionId);
}
