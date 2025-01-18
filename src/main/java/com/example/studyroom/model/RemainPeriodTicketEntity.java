package com.example.studyroom.model;

import com.example.studyroom.common.DurationConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@DiscriminatorValue("REMAIN_PERIOD")
public class RemainPeriodTicketEntity extends RemainTicketEntity{ //기간권


    @Column(nullable = false)
    private OffsetDateTime endDate;


}
