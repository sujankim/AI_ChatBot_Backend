package com.sujan.chatbot.backend.repository;

import com.sujan.chatbot.backend.model.ChatSession;
import com.sujan.chatbot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    // Only return chats belonging to a specific user
    List<ChatSession> findByUserOrderByCreatedAtDesc(User user);

    // Find by ID AND user (prevents users accessing other users' chats)
    Optional<ChatSession> findByIdAndUser(Long id, User user);

    // Check ownership before delete
    boolean existsByIdAndUser(Long id, User user);
}
