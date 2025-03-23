package com.example.studyroom.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatHistoryPageRequestDto {
    private Long roomId;
    private int page; // 0부터 시작
    private int size; // 페이지당 메시지 수
}
