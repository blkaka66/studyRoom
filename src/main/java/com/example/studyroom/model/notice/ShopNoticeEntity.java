package com.example.studyroom.model.notice;

import com.example.studyroom.model.BaseEntity;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "shop_notice")
public class ShopNoticeEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopId", foreignKey = @ForeignKey(name = "fk_shop_id"))
    private ShopEntity shop;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeType noticeType;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;  // 생성 날짜

    @Column(nullable = false)
    private Boolean isRead = false;

}
