package com.example.studyroom.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class TicketHistoryDTO {
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Boolean expired;

    public TicketHistoryDTO(OffsetDateTime startDate, OffsetDateTime endDate, Boolean expired) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.expired = expired;
    }
}
