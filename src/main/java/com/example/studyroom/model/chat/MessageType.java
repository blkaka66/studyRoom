package com.example.studyroom.model.chat;

public enum MessageType {
    CHAT,         // 일반 채팅 메시지
    LEAVE,        // 퇴장 메시지 (ex: "상대방이 나갔습니다")
    ENTER,    //입장 메시지(EX:"상대방이 입장했습니다")
    TYPING;   // 입력중 메시지 (ex: "상대방이 입력중입니다")

    public static MessageType fromString(String type) {
        if (type == null) {
            return CHAT; // 기본값
        }
        try {
            return MessageType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CHAT; // 잘못된 값이면 기본값으로 CHAT 처리
        }
    }

}
