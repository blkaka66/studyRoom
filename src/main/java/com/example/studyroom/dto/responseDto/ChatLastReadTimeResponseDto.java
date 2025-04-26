
package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatLastReadTimeResponseDto {
    private Long userId;
    private String userType;
    private String timestamp;
}
