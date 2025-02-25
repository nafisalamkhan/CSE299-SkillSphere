package com.cse299.skillSphere.messages.OneToOne.ChatRoom;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_room")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {
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
}
