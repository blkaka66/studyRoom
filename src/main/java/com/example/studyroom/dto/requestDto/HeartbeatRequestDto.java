package com.example.studyroom.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatRequestDto {
    private Long userId;
    private String userType;
    private String timestamp;
}
