package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@Entity
@Table(name = "time_ticket")
//@DiscriminatorValue("TIME")
//시간권
public class TimeTicketEntity extends TicketEntity{
//    @Column(nullable = true)  // TimeTicketEntity에서는 null 가능
//    private int days; // 기간 (nullable)

    @Column(nullable = false)
    private int hours;
}
