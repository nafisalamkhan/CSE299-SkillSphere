package com.cse299.skillSphere.messages.OneToOne.Chat;

import com.cse299.skillSphere.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findBySenderAndRecipient(User sender, User recipient);
}
