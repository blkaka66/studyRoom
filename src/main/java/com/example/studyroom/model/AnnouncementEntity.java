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

    private String title;

    private String content;

    private OffsetDateTime createdAt;
}
