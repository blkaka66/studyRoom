package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@Entity
@Table(name = "periodTicket")
//기간권
public class PeriodTicketEntity extends TicketEntity{
    @Column(nullable = false)
    private int days;
}
