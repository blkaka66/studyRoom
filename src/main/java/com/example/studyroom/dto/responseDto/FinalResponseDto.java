package com.example.studyroom.dto.responseDto;

import com.example.studyroom.type.ApiResult;
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

    public static <T> FinalResponseDto<T> success(T data) {
        return FinalResponseDto.of(ApiResult.SUCCESS, data);
    }

    public static <T> FinalResponseDto<T> of(ApiResult apiResult, T data) {
        return FinalResponseDto.<T>builder()
                .message(apiResult.getMessage())
                .statusCode(apiResult.getCode())
                .data(data)
                .build();
    }

    public static <T> FinalResponseDto<T> of(ApiResult apiResult) {
        return FinalResponseDto.<T>builder()
                .message(apiResult.getMessage())
                .statusCode(apiResult.getCode())
                .build();
    }
}
