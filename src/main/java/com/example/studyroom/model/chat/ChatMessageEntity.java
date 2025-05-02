package com.example.studyroom.model.chat;

import com.example.studyroom.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity

public class ChatMessageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoomEntity room;


    private Long senderId;
    private String senderType;

    private Long receiverId;
    private String receiverType;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String message;
    private LocalDateTime timestamp;

    @Builder
    public ChatMessageEntity(ChatRoomEntity room, Long senderId, String senderType, Long receiverId, String receiverType,
                             String message, MessageType messageType, LocalDateTime timestamp) {
        this.room = room;
        this.senderId = senderId;
        this.senderType = senderType;
        this.receiverId = receiverId;
        this.receiverType = receiverType;
        this.message = message;
        this.messageType = messageType;
        this.timestamp = timestamp;
    }

    public ChatMessageEntity() {
        // 기본 생성자
    }
}
