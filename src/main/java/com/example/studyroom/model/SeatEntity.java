package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seat")

public class SeatEntity extends BaseEntity{
    @Column(nullable = false, unique = true)
    private String seatCode;

    @Column(nullable = false)
    private Boolean onService;
}
