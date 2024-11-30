package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.MemberResponseDto;

import com.example.studyroom.dto.responseDto.PaymentHistoryDto;
import com.example.studyroom.dto.responseDto.ShopListResponseDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.security.JwtUtil;
import com.example.studyroom.security.SecurityUtil;
import com.example.studyroom.service.MailService;
import com.example.studyroom.service.MemberService;

import com.example.studyroom.service.ShopService;
import com.example.studyroom.service.TicketService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {


    private final MemberService memberService;
    private final MailService mailService;
    private final ShopService shopService;
    private final TicketService ticketService;

    public MemberController(
           MemberService memberService,
            MailService mailService,
           ShopService shopService,
           TicketService ticketService
        ) {
        this.memberService = memberService;
        this.mailService = mailService;
        this.shopService = shopService;
        this.ticketService=ticketService;
    }

    @PostMapping("/emails/verification-requests")//이건 나중에 전화번호로 바꾸기
    public ResponseEntity sendMessage(@RequestParam("email") String email) {
        System.out.println("ddddddddd");
        mailService.sendEmail(email, "안녕하세요", "반갑습니다");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<FinalResponseDto<String>> login(@RequestBody MemberSignInRequestDto member, HttpServletResponse response) {
        FinalResponseDto<String> token = memberService.login(member,response);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/in")
    public ResponseEntity<FinalResponseDto<String>> in(@RequestBody OccupySeatRequestDto occupySeatRequestDto) {
        MemberEntity member = SecurityUtil.getMemberInfo();
        return ResponseEntity.ok(memberService.occupySeat(member.getShop().getId(), occupySeatRequestDto.getRoomName(), occupySeatRequestDto.getSeatCode(), member.getId()));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<FinalResponseDto<MemberEntity>> signUp(@RequestBody MemberSignUpRequestDto member) {
        // signUp 로직을 구현한 후, 성공 메시지와 함께 생성된 ShopEntity를 반환
        System.out.println("회원가입 요청: " + member);
        FinalResponseDto<MemberEntity> createdMember = memberService.signUp(member);
        return ResponseEntity.ok(createdMember);
    }

    @GetMapping("/sign-in/shop-list")
    public ResponseEntity<FinalResponseDto<List<ShopListResponseDto>>> getShopList() {
        FinalResponseDto<List<ShopEntity>> shopResponse = shopService.getShopList();
        List<ShopListResponseDto> shopDtos = ShopListResponseDto.of(shopResponse.getData());
        return ResponseEntity.ok(
                FinalResponseDto.<List<ShopListResponseDto>>builder()
                        .message("상점 목록을 성공적으로 가져왔습니다.")
                        .statusCode("0000")
                        .data(shopDtos)
                        .build()
        );
    }

//    @GetMapping("/emails/verifications")
//    public ResponseEntity verificationEmail(@RequestParam("email") @CustomEmail String email,
//                                            @RequestParam("code") String authCode) {
//        EmailVerificationResult response = memberService.verifiedCode(email, authCode);
//
//        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
//    }
//
//
//    @GetMapping("/{userId}/ticket")
//    public FinalResponseDto getMemberRemainTime(@PathVariable("userId") Long userId) {
//        //TODO: 쿠키에서 shopId추출하는 메서드추가
//        return this.memberService.getRemainTime( shopId, userId);
//
//    }
//
//    @PostMapping("/in")
//    public FinalResponseDto occupySeat(@RequestBody OccupySeatRequestDto requestDto) {
//        //TODO: 쿠키에서 shopid와 memberid 추출하는 메서드추가
//        return shopService.occupySeat(
//                requestDto.getRoomName(),
//                requestDto.getSeatCode()
//        );
//    }
//
//    @PatchMapping("/out")
//    public FinalResponseDto out() {
//        //TODO: 쿠키에서 userid, customerId 추출하는 메서드추가
//        return this.memberService.out(userId);
//
//    }
//
//    @PostMapping("/move")
//    public FinalResponseDto move(@RequestBody MemberMoveRequestDto requestDto) {
//        //TODO: 쿠키에서 userid, customerId 추출하는 메서드추가
//        return this.memberService.move(
//                userId,
//                requestDto.getMovingRoomCode(),
//                requestDto.getMovingSeatNumber()
//        );
//    }
    @GetMapping("/getUserInfo")
    public ResponseEntity<FinalResponseDto<MemberResponseDto>> getMemberInfo() {
        MemberEntity member = JwtUtil.getMember();
        FinalResponseDto<MemberResponseDto> response = this.memberService.getMemberInfo(member.getId());
        return ResponseEntity.ok(response);

    }
//    @DeleteMapping("/delete")
//    public FinalResponseDto<String> deleteMember() {
//        //TODO: 쿠키에서 userId추출하는 메서드추가
//        return this.memberService.deleteMember(userId);
//
//    }


    @GetMapping("/pay-info")
    public ResponseEntity<FinalResponseDto<PaymentHistoryDto>> getPaymentHistory() {
        MemberEntity member = JwtUtil.getMember();
        FinalResponseDto<PaymentHistoryDto> response = this.ticketService.getPaymentHistory(member.getShop().getId(), member.getId());

        return ResponseEntity.ok(response);
    }
}
 //TODO: 자리 점유 요청
//회원 회원가입 , 로그인,회원정보 가져오기 , 회원탈퇴요청,로그아웃
