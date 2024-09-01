package com.example.studyroom.controller;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.MemberResponseDto;
import com.example.studyroom.service.MailService;
import com.example.studyroom.service.MemberService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/member")
public class MemberController {


    private final MemberService memberService;
    private final MailService mailService;



    public MemberController(
           MemberService memberService,
            MailService mailService) {
        this.memberService = memberService;
        this.mailService = mailService;
    }

    @PostMapping("/emails/verification-requests")
    public ResponseEntity sendMessage(@RequestParam("email") String email) {
        System.out.println("ddddddddd");
        mailService.sendEmail(email, "안녕하세요", "반갑습니다");
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
    @GetMapping("/{userId}/ticket")
    public FinalResponseDto<MemberResponseDto> getMemberInfo(@PathVariable("userId") Long userId) {
        //TODO: 쿠키에서 userId추출하는 메서드추가
        return this.memberService.getMemberInfo( userId);

    }
    @DeleteMapping("/delete")
    public FinalResponseDto<String> deleteMember() {
        //TODO: 쿠키에서 userId추출하는 메서드추가
        return this.memberService.deleteMember(userId);

    }

}
 //TODO: 자리 점유 요청
//회원 회원가입 , 로그인,회원정보 가져오기 , 회원탈퇴요청,로그아웃
