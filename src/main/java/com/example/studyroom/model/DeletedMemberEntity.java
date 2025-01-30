package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity

@Table(name = "delete_member")
public class DeletedMemberEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "FK_SHOP_ID"))
    private ShopEntity shop;

    @Column(nullable = false, unique = false) // @UniqueConstraint로 유니크 처리
    private String phone;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private OffsetDateTime deleteTime;

    @Builder
    public DeletedMemberEntity(ShopEntity shop, String name, String phone, String password , OffsetDateTime deleteTime) {
        this.shop = shop;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.deleteTime = deleteTime;
    }
    public DeletedMemberEntity() {
    }
}
