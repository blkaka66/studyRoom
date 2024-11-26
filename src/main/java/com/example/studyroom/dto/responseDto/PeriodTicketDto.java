package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.PeriodTicketEntity;
import com.example.studyroom.model.TicketTypeEnum;
import com.example.studyroom.model.TimeTicketEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@Builder
public class PeriodTicketDto {
    private String name;
    private int amount;
    private int days;
}
