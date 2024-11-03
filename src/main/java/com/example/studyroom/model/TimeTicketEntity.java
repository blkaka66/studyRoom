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
@DiscriminatorValue("TIME")
//시간권
public class TimeTicketEntity extends TicketEntity{
    @Column(nullable = false)
    private int hours;
}
