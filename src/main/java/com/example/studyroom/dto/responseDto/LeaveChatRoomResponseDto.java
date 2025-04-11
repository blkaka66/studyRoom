package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LeaveChatRoomResponseDto {
    private Long chatRoomId;
}
