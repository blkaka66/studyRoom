package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.PeriodTicketEntity;
import com.example.studyroom.model.TicketEntity;
import com.example.studyroom.model.TimeTicketEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Data
public class ProductResponseDto {
    private List<PeriodTicketDto> periodTicketList;
    private List<TimeTicketDto> timeTicketList;

    @Builder
    public ProductResponseDto(List<PeriodTicketEntity> periodTicketList, List<TimeTicketEntity> timeTicketList) {
        this.periodTicketList = periodTicketList.stream()
                .map(x -> PeriodTicketDto.builder()
                        .id(x.getId())
                        .amount(x.getAmount())
                        .ticketType(x.getTicketType())
                        .name(x.getName())
                        .days(x.getDays())
                        .build())
                .collect(Collectors.toList());

        this.timeTicketList = timeTicketList.stream()
                .map(x -> TimeTicketDto.builder()
                        .id(x.getId())
                        .amount(x.getAmount())
                        .ticketType(x.getTicketType())
                        .name(x.getName())
                        .hours(x.getHours())
                        .build())
                .collect(Collectors.toList());
    }
}
