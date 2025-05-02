package com.example.studyroom.model.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId; //채팅방 시작한사람
    private String senderType;

    private Long partnerId;
    private String partnerType;

    private LocalDateTime createdAt = LocalDateTime.now();


    private Boolean senderClosed;
    private Boolean partnerClosed;
}
