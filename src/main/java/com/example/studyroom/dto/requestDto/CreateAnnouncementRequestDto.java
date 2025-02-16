package com.example.studyroom.dto.requestDto;

import com.example.studyroom.model.AnnouncementEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateAnnouncementRequestDto {
    private String title;
    private String content;
    private AnnouncementEnum announcementType;
}
