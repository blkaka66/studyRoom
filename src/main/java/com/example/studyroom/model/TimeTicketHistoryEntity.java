package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@DiscriminatorValue("TIME_HISTORY")
public class TimeTicketHistoryEntity extends TicketHistoryEntity<TimeTicketEntity>{
}
