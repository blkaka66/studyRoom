package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.MemberMoveRequestDto;
import com.example.studyroom.dto.requestDto.OccupySeatRequestDto;
import com.example.studyroom.dto.responseDto.MessageResponseDto;
import com.example.studyroom.dto.responseDto.RoomAndSeatInfoResponseDto;
import com.example.studyroom.service.MemberService;
import com.example.studyroom.service.ShopService;
import org.springframework.web.bind.annotation.*;

public class MemberController {


    private final MemberService memberService;



    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @GetMapping("/member/{userId}/ticket")
    public MessageResponseDto getMemberRemainTime(@PathVariable("userId") Long userId) {
        //TODO: 쿠키에서 shopId추출하는 메서드추가
        return this.memberService.getRemainTime( shopId, userId);

    }
    @PatchMapping("/memeber/out")
    public MessageResponseDto out() {
        //TODO: 쿠키에서 userid, customerId 추출하는 메서드추가
        return this.memberService.out(userId);

    }

    @PostMapping("/memeber/move")
    public MessageResponseDto move(@RequestBody MemberMoveRequestDto requestDto) {
        //TODO: 쿠키에서 userid, customerId 추출하는 메서드추가
        return this.memberService.out(userId,requestDto.getCurrentRoomCode(),requestDto.getMovingRoomCode(),requestDto.getCurrentSeatNumber(),requestDto.getMovingSeatNumber());
    }
}
//회원 회원가입 , 로그인,회원정보 가져오기 , 회원탈퇴요청,로그아웃
