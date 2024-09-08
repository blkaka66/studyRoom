package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.PeriodTicketEntity;
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
public class ProductResponseDto {
    private List<PeriodTicketEntity> periodTicketEntities;
    private List<TimeTicketEntity> timeTicketEntities;

}
