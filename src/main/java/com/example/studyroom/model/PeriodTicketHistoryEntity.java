package com.example.studyroom.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
//기간권 티켓을 산 기록
@Getter
@Setter
@Entity
@DiscriminatorValue("PERIOD_HISTORY")
public class PeriodTicketHistoryEntity extends TicketHistoryEntity<PeriodTicketEntity>{

}
