package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.MySeatInfoResponseDto;
import com.example.studyroom.dto.responseDto.RemainTimeResponseDto;
import com.example.studyroom.model.EnterHistoryEntity;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface MemberService extends BaseService<MemberEntity> {
    List<MemberEntity> findByShop(ShopEntity shop);



    // 로그인했을때 남아있는 티켓시간 가져오는 메서드 (시간권이면 remainTime, 기간권이면 endTime 가져오기(쿠키나 토큰에서 id들 뽑아올예정))
//    Duration getRemainTime(Long shopId, Long userId);
    FinalResponseDto getRemainTime(Long shopId, Long userId);

    FinalResponseDto<MySeatInfoResponseDto> getSeatId(Long userId); //회원id받아서 어떤자리에 앉았는지 추출

    FinalResponseDto<String> login(MemberSignInRequestDto dto, HttpServletResponse response); //로그인 기능

    FinalResponseDto<String> logout(MemberEntity member ,String accessToken);

    FinalResponseDto<MemberEntity> signUp(MemberSignUpRequestDto member); //회원가입

    FinalResponseDto<String> occupySeat( MemberEntity member,OccupySeatRequestDto requestDto); //자리 점유요청 메서드

    FinalResponseDto out(Long userId);//퇴장

    FinalResponseDto<String> move(MemberEntity member,MemberMoveRequestDto requestDto);//자리이동

    FinalResponseDto getMemberInfo(Long userId);//회원 정보가져오기

    FinalResponseDto<String> deleteMember(Long userId);//회원탈퇴



}
