package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member")
public class MemberEntity extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "FK_SHOP_ID"))
    private ShopEntity shop;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Builder
    public MemberEntity(ShopEntity shop, String name,String phone,String password) {
        this.shop = shop;
        this.name = name;
        this.phone = phone;
        this.password = password;

    }


    public MemberEntity() {

    }
}
