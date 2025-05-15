package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter

public class NotificationResponseDto {
    private Long id;
    private String title;
    private String content;
    private String noticeType;
    private boolean isRead;
    private OffsetDateTime createdAt;
}
