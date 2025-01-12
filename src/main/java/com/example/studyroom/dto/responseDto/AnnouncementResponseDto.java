package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.AnnouncementEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder

public class AnnouncementResponseDto {
    private String title;
    private String content;
    private OffsetDateTime createdAt;


    public static AnnouncementResponseDto convertToDto(AnnouncementEntity entity) {
        return AnnouncementResponseDto.builder()
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
