package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;

import java.util.List;

public interface ShopService extends BaseService<ShopEntity> {
    boolean existsByEmail(String email);
   // List<MemberEntity> getMemberList(Long shopId);
   FinalResponseDto<List<MemberResponseDto>> getMemberList(Long shopId);
    //List<ShopEntity> getShopList();
    FinalResponseDto getShopList();
//    List<ShopListResponseDto> getShopListResponseDto(Long shopId);

    //ShopEntity login(String username, String password);
    FinalResponseDto login(String username, String password);

    //ShopEntity signUp(ShopSignUpRequestDto dto); //회원가입
    FinalResponseDto<ShopEntity> signUp(ShopSignUpRequestDto dto); //회원가입

    //ShopInfoResponseDto getShopInfo(Long shopId);
    FinalResponseDto<ShopInfoResponseDto> getShopInfo(Long shopId);


    //List<RoomAndSeatInfoResponseDto> getRoomsAndSeatsByShopId(Long shopId,Long customerId); //shopid,customerId받아서 방이랑 좌석정보 가져오기
    FinalResponseDto getRoomsAndSeatsByShopId(Long shopId,Long customerId); //shopid,customerId받아서 방이랑 좌석정보 가져오기

    FinalResponseDto<List<ProductResponseDto>> getProductList(Long shopId , String productType);//티켓 정보가져오기(시간권 기간권나눠서)

}
