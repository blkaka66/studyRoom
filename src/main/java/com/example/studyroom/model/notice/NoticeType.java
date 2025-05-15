package com.example.studyroom.model.notice;

import com.example.studyroom.model.chat.MessageType;

public enum NoticeType {
    EXPIRED,
    ALERT,
    CHAT,
    SIGNUP,
    GENERAL;

    public static NoticeType fromString(String type) {
        if (type == null) {
            return GENERAL; // 기본값
        }
        try {
            return NoticeType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GENERAL; // 잘못된 값이면 기본값으로 GENERAL 처리
        }
    }
}
