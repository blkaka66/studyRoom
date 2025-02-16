package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "announcement")
public class AnnouncementEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopId", foreignKey = @ForeignKey(name = "fk_shop_id"))
    private ShopEntity shop;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = true)
    private OffsetDateTime updatedAt;

    @Column(nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)  // Enum으로 ticket_type 지정
    @Column(name = "announcement_type", nullable = false)
    private AnnouncementEnum announcementType = AnnouncementEnum.GENERAL;

}
