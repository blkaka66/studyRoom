package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "seat_expiration_alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatExpirationAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long seatId;

    private Long shopId;

    @Column(nullable = false)
    private String ticketType; // "PERIOD" 또는 "TIME"

    @Column(nullable = false)//자리 이용이 종료되기 10분 전에 푸시 알림을 보내는시간
    private OffsetDateTime sendTime;

    private Boolean sent = false;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
