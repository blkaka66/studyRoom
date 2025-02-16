package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.AnnouncementEntity;
import com.example.studyroom.model.AnnouncementEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder

public class AnnouncementResponseDto {
    private String title;
    private String content;
    private OffsetDateTime createdAt;
    private long id;
    private AnnouncementEnum announcementType;
    public static AnnouncementResponseDto convertToDto(AnnouncementEntity entity) {
        return AnnouncementResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .announcementType(entity.getAnnouncementType())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
