package com.example.studyroom.service;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;

import java.util.List;

public interface MemberService extends BaseService<MemberEntity> {
    List<MemberEntity> findByShop(ShopEntity shop);
    MemberEntity login(String username, String password);


}
