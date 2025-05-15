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
@DiscriminatorValue("REMAIN_TIME")
public class RemainTimeTicketEntity extends RemainTicketEntity {

    @Convert(converter = DurationConverter.class)
    @Column(nullable = false)
    private Duration remainTime;


}
