package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ShopDailyPaymentResponseDto {

    private Long shopId;  // shop의 ID만 포함
    private int year;
    private int month;
    private int day;
    private DayOfWeek dayOfWeek;
    private int totalAmount;  // 총 결제액
    private String ticketType;  // TicketTypeEnum을 String으로 변환하여 저장

    // ShopDailyPaymentEntity에서 필요한 정보만 추출하여 DTO로 변환
    public static ShopDailyPaymentResponseDto fromEntity(ShopDailyPaymentEntity entity) {
        return ShopDailyPaymentResponseDto.builder()
                .shopId(entity.getShop().getId())  // shopId만 포함
                .year(entity.getYear())
                .month(entity.getMonth())
                .day(entity.getDay())
                .dayOfWeek(entity.getDayOfWeek())
                .totalAmount(entity.getTotalAmount())
                .ticketType(entity.getTicketType().name())  // TicketTypeEnum을 String으로 변환
                .build();
    }

}
