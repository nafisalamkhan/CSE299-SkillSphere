package com.cse299.skillSphere.messages.OneToOne.ChatRoom;

import com.cse299.skillSphere.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(User sender, User recipient, boolean createNewRoomIfNotExist) {
        return chatRoomRepository.findBySenderIdAndRecipientId(sender.getId().toString(), recipient.getId().toString())
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExist) {
                        String chatId = createChatId(sender.getId().toString(), recipient.getId().toString());
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    private String createChatId(String senderID, String recipientID) {
        var chatId = String.format("%s_%s", senderID, recipientID);

        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderID)
                .recipientId(recipientID)
                .build();
        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientID)
                .recipientId(senderID)
                .build();
        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);
        return chatId;
    }
}
