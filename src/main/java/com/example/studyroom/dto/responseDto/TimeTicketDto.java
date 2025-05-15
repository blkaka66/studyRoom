package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.TicketTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class TimeTicketDto {
    private long id;
    private String name;
    private int amount;
    private TicketTypeEnum ticketType;
    private int hours;
    private int validDays;
}
