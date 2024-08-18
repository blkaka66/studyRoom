package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FinalResponseDto<T> {
    private String message;
    private String statusCode;
    private T data;
    // 정적 팩토리 메서드 추가
    public static <T> FinalResponseDto<T> of(String message, String statusCode, T data) {
        return FinalResponseDto.<T>builder()
                .message(message)
                .statusCode(statusCode)
                .data(data)
                .build();
    }
}
