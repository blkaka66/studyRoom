package com.example.studyroom.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketDTO {
    private String type;
    private String name;
    private int amount;
    private int period;

    public TicketDTO(String type, String name, int amount, int period) {
        this.type = type;
        this.name = name;
        this.amount = amount;
        this.period = period;
    }
}
