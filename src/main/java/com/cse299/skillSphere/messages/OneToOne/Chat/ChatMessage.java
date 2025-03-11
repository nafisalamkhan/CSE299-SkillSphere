package com.cse299.skillSphere.messages.OneToOne.Chat;


import com.cse299.skillSphere.messages.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Entity
@Table(name = "chat_message")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "chatId")
    private String chatId;

    @Column(name = "senderId")
    private String senderId;

    @Column(name = "recipientId")
    private String recipientId;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    private Date timestamp;

    // group discussion
    @Column(name = "message_type")
    private MessageType type;
}
