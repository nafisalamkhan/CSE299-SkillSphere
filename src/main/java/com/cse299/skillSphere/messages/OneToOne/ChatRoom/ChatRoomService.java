package com.cse299.skillSphere.messages.OneToOne.ChatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    public Optional<String> getChatRoomId(String senderID,
                                          String recipientID,
                                          boolean createNewRoomIfNotExist){
        return chatRoomRepository.findBySenderIdAndRecipientId(senderID, recipientID)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExist){
                        var chatId = createChatId(senderID, recipientID);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    private String createChatId(String senderID, String recipientID) {
        var chatId = String.format("%s_%s", senderID, recipientID);         //nafis_aciea

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
