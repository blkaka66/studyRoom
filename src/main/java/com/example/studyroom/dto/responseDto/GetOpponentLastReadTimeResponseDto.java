package com.example.studyroom.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetOpponentLastReadTimeResponseDto {
    private String lastReadTimestamp;
}
