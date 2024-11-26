package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class TimeTicketDto {
    private String name;
    private int amount;
    private int hours;
}
