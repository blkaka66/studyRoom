package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.ShopListResponseDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;

import java.util.List;

public interface ShopService extends BaseService<ShopEntity> {
    boolean existsByEmail(String email);
    List<MemberEntity> getMemberList(Long shopId);

    List<ShopEntity> getShopList(Long shopId);

    List<ShopEntity> findByid(Long shopId);

    List<ShopListResponseDto> getShopListResponseDto(Long shopId);

    ShopEntity login(String username, String password);

    ShopEntity signUp(ShopSignUpRequestDto dto); //회원가입

    ShopEntity getShopInfo(Long shopId);

}
