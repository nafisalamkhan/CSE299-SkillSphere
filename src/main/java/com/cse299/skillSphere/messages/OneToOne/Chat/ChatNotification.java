package com.cse299.skillSphere.messages.OneToOne.Chat;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "chat_notification")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor@Builder
public class ChatNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "senderId")
    private String senderId;

    @Column(name = "recipientId")
    private String recipientId;

    @Column(name = "content")
    private String content;
}
