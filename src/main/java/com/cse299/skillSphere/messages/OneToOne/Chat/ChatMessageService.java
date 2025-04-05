package com.cse299.skillSphere.messages.OneToOne.Chat;

import com.cse299.skillSphere.messages.OneToOne.ChatRoom.ChatRoomService;
import com.cse299.skillSphere.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        String chatId = chatRoomService.getChatRoomId(
                chatMessage.getSender(),
                chatMessage.getRecipient(),
                true
        ).orElseThrow();

        chatMessage.setChatId(chatId);
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(User sender, User recipient) {
        return repository.findBySenderAndRecipient(sender, recipient);
    }

}
