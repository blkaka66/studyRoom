package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "shop")
public class ShopEntity extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String password;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "shop")
    private List<MemberEntity> members = new ArrayList<>();

    @Builder
    public ShopEntity(Long id, String location, String name,String email,String password) {
        setId(id);
        this.location = location;
        this.name = name;
        this.email = email;
        this.password = password;

    }

    public ShopEntity() { //난 기본생성자가 필요없는데 이걸 만들어야만 하나?

    }
}

