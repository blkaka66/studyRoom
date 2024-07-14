package com.example.studyroom.service;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;

import java.util.List;

public interface ShopService extends BaseService<ShopEntity> {
    boolean existsByEmail(String email);
    List<MemberEntity> getMemberList(Long shopId);
}
