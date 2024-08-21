package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.MemberMoveRequestDto;
import com.example.studyroom.dto.requestDto.OccupySeatRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/member")
public class MemberController {


    private final MemberService memberService;



    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/emails/verification-requests")
    public ResponseEntity sendMessage(@RequestParam("email") @Valid @CustomEmail String email) {
        memberService.sendCodeToEmail(email);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/emails/verifications")
    public ResponseEntity verificationEmail(@RequestParam("email") @CustomEmail String email,
                                            @RequestParam("code") String authCode) {
        EmailVerificationResult response = memberService.verifiedCode(email, authCode);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }


    @GetMapping("/{userId}/ticket")
    public FinalResponseDto getMemberRemainTime(@PathVariable("userId") Long userId) {
        //TODO: 쿠키에서 shopId추출하는 메서드추가
        return this.memberService.getRemainTime( shopId, userId);

    }

    @PostMapping("/in")
    public FinalResponseDto occupySeat(@RequestBody OccupySeatRequestDto requestDto) {
        //TODO: 쿠키에서 shopid와 memberid 추출하는 메서드추가
        return shopService.occupySeat(
                requestDto.getRoomName(),
                requestDto.getSeatCode()
        );
    }

    @PatchMapping("/out")
    public FinalResponseDto out() {
        //TODO: 쿠키에서 userid, customerId 추출하는 메서드추가
        return this.memberService.out(userId);

    }

    @PostMapping("/move")
    public FinalResponseDto move(@RequestBody MemberMoveRequestDto requestDto) {
        //TODO: 쿠키에서 userid, customerId 추출하는 메서드추가
        return this.memberService.move(
                userId,
                requestDto.getMovingRoomCode(),
                requestDto.getMovingSeatNumber()
        );
    }
}
// TODO: 자리 점유 요청
//회원 회원가입 , 로그인,회원정보 가져오기 , 회원탈퇴요청,로그아웃
