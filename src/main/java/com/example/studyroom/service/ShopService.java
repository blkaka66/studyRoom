package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.RemainTimeResponseDto;
import com.example.studyroom.dto.responseDto.RoomAndSeatInfoResponseDto;
import com.example.studyroom.dto.responseDto.ShopInfoResponseDto;
import com.example.studyroom.dto.responseDto.ShopListResponseDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;

import java.util.List;

public interface ShopService extends BaseService<ShopEntity> {
    boolean existsByEmail(String email);
    List<MemberEntity> getMemberList(Long shopId);

    List<ShopEntity> getShopList();

    List<ShopListResponseDto> getShopListResponseDto(Long shopId);

    ShopEntity login(String username, String password);

    ShopEntity signUp(ShopSignUpRequestDto dto); //회원가입

    ShopInfoResponseDto getShopInfo(Long shopId);

    RoomAndSeatInfoResponseDto getRoomsAndSeatsByShopId(Long shopId,Long customerId); //shopid,customerId받아서 방이랑 좌석정보 가져오기

    boolean occupySeat(Long shopId , String roomName, int seatCode, Long memberId, Long ticketHistoryId); //자리 점유요청 메서드
}
