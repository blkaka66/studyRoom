package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatListUpdateResponseDto {
    private Long roomId;
    private String message;
    private String timestamp;

}
