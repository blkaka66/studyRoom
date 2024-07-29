package com.example.studyroom.dto.responseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Getter
@Setter
@Builder
public class RemainTimeResponseDto {
    private String ticketCategory;
    private String ticketExpireTime; // JSON에서 직접 받아오는 문자열


    // 날짜 형식을 판별하고 변환하는 메서드
    public LocalDateTime getParsedTicketExpireTime() {
        try {
            // 날짜 형식으로 변환
            return LocalDateTime.parse(ticketExpireTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            // 날짜 형식이 아닌 경우 처리 (예: "8시간")
            // 시간 단위 문자열을 LocalDateTime으로 변환할 필요가 있는 경우 여기에 구현
            return null;
        }
    }

    // 시간권을 Duration 형식으로 변환하는 메서드
    public Duration getParsedDuration() {
        try {
            if (ticketExpireTime.endsWith("시간")) {
                int hours = Integer.parseInt(ticketExpireTime.replace("시간", "").trim());
                return Duration.ofHours(hours);
            }
        } catch (NumberFormatException e) {
            // 숫자 형식이 아닌 경우 처리
        }
        return null;
    }
}
