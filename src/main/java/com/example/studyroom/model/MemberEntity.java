package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(name = "uk_phone_shop", columnNames = {"phone", "shop_id"})
})
public class MemberEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "FK_SHOP_ID"))
    private ShopEntity shop;

    @Column(nullable = false, unique = false) // @UniqueConstraint로 유니크 처리
    private String phone;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;  // 가입 날짜

    @Column(nullable = true)
    private OffsetDateTime lastEnterTime;  // 최근 로그인 시간

    @Builder
    public MemberEntity(ShopEntity shop, String name, String phone, String password ,OffsetDateTime createdAt) {
        this.shop = shop;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.createdAt = createdAt;
    }

    public MemberEntity() {
    }

    // 회원이 처음 생성될 때 가입 날짜 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    // 최근 로그인 시간 업데이트
    public void updateLastEnterTime() {
        this.lastEnterTime = OffsetDateTime.now();
    }
}
