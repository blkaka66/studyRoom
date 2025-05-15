package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ticket_expiration_alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketExpirationAlertEntity extends BaseEntity {

    private Long memberId;

    private Long shopId;

    @Column(nullable = false)
    private String ticketType; // "PERIOD" 또는 "TIME"

    @Column(nullable = false)//티켓이 만료되기 10분 전에 푸시 알림을 보내는시간
    private OffsetDateTime sendTime;

    private Boolean sent = false;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
