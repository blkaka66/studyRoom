package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@Entity
@Table(name = "period_ticket")
//@DiscriminatorValue("PERIOD")
//기간권
public class PeriodTicketEntity extends TicketEntity{
    @Column(nullable = false)
    private int days;
//
//    @Column(nullable = true)  // PeriodTicketEntity에서는 null 가능
//    private int hours; // 기간 (nullable)
}
