package com.cse299.skillSphere.messages.OneToOne.Chat;

import ch.qos.logback.core.model.Model;
import com.cse299.skillSphere.models.User;
import com.cse299.skillSphere.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload ChatMessage chatMessage
    ) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessage.getRecipient().getId()), "/queue/messages",
                ChatNotification.builder()
                        .id(savedMsg.getId())
                        .senderId(String.valueOf(savedMsg.getSender().getId())) // Convert Long to String
                        .recipientId(String.valueOf(savedMsg.getRecipient().getId())) //  Convert Long to String
                        .content(savedMsg.getContent())
                        .build()
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessage(
            @PathVariable Long senderId,
            @PathVariable Long recipientId
    ) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User recipient = userRepository.findById(recipientId).orElseThrow();
        return ResponseEntity.ok(chatMessageService.findChatMessages(sender, recipient));
    }



    // group messaging
    @MessageMapping("/chat.sendMessage")
    @SendTo("user/public")
    public ChatMessage sendMessage(
            @Payload ChatMessage chatMessage
    ){
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("user/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ){
        // adding username in ws session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getId());
        return chatMessage;
    }
}
