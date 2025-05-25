package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.*;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.security.JwtUtil;
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
    private final MemberRepository memberRepository;

    public MemberController(
            MemberService memberService,
            MailService mailService,
            ShopService shopService,
            TicketService ticketService,
            MemberRepository memberRepository) {
        this.memberService = memberService;
        this.mailService = mailService;
        this.shopService = shopService;
        this.ticketService = ticketService;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/emails/verification-requests")//이건 나중에 전화번호로 바꾸기
    public ResponseEntity sendMessage(@RequestParam("email") String email) {
        System.out.println("ddddddddd");
        mailService.sendEmail(email, "안녕하세요", "반갑습니다");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<FinalResponseDto<String>> login(@RequestBody MemberSignInRequestDto member, HttpServletResponse response) {
        FinalResponseDto<String> token = memberService.login(member, response);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/occupy-seat")
    public ResponseEntity<FinalResponseDto<String>> occupySeat(@RequestBody OccupySeatRequestDto requestDto) {
        System.out.println("^^^");
        // MemberEntity member = SecurityUtil.getMemberInfo();
        MemberEntity member = JwtUtil.getMember();
        return ResponseEntity.ok(memberService.occupySeatAndHandleTicket(member, requestDto));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<FinalResponseDto<MemberEntity>> signUp(@RequestBody MemberSignUpRequestDto member) {

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


    @PostMapping("/out")
    public FinalResponseDto<String> out() {
        MemberEntity member = JwtUtil.getMember();
        return this.memberService.out(member.getId());
    }

    //
    @PostMapping("/move")
    public FinalResponseDto move(@RequestBody MemberMoveRequestDto requestDto) {
        //TODO: 쿠키에서 userid, customerId 추출하는 메서드추가
        MemberEntity member = JwtUtil.getMember();
        return this.memberService.moveAndHandleTicket(
                member,
                requestDto
        );
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<FinalResponseDto<MemberResponseDto>> getMemberInfo() {
        MemberEntity member = JwtUtil.getMember();

        FinalResponseDto<MemberResponseDto> response = this.memberService.getMemberInfo(member.getId());
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/delete")
    public FinalResponseDto<String> deleteMember(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7); // "Bearer "를 제외한 토큰만 추출
        }
        MemberEntity member = JwtUtil.getMember();
        return this.memberService.deleteMemberAndLogout(member, accessToken);
    }


    @GetMapping("/getRemainTime")
    public ResponseEntity<FinalResponseDto<RemainTicketInfoResponseDto>> getRemainTime(Long userId) {
        MemberEntity member = JwtUtil.getMember();
        FinalResponseDto<RemainTicketInfoResponseDto> response = this.memberService.getRemainTime(member.getShop().getId(), member.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logOut")
    public ResponseEntity<FinalResponseDto<String>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        MemberEntity member = JwtUtil.getMember();
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7); // "Bearer "를 제외한 토큰만 추출
        }
        FinalResponseDto<String> response = this.memberService.outAndlogout(member, accessToken);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/reset/pw")
    public ResponseEntity<FinalResponseDto<String>> resetPw(@RequestBody ResetPwRequestDto requestDto, @RequestHeader("Authorization") String authorizationHeader) {
        MemberEntity member = JwtUtil.getMember();
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7); // "Bearer "를 제외한 토큰만 추출
        }
        FinalResponseDto<String> response = this.memberService.resetPwAndLogout(member, requestDto, accessToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/pay-info")
    public ResponseEntity<FinalResponseDto<PaymentHistoryDto>> getPaymentHistory(@RequestBody PaymentHistoryDateRequestDto requestDto) {
        MemberEntity member = JwtUtil.getMember();
        FinalResponseDto<PaymentHistoryDto> response = this.ticketService.getPaymentHistory(requestDto, member.getShop().getId(), member.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findMySeat")
    public ResponseEntity<FinalResponseDto<MySeatInfoResponseDto>> getSeatId() {
        MemberEntity member = JwtUtil.getMember();
        FinalResponseDto<MySeatInfoResponseDto> response = this.memberService.getSeatId(member.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-notifications")
    public ResponseEntity<FinalResponseDto<List<NotificationResponseDto>>> getNotifications() {
        MemberEntity member = JwtUtil.getMember();
        FinalResponseDto<List<NotificationResponseDto>> response = this.memberService.getNotifications(member.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-notification-detail/{notificationId}")
    public ResponseEntity<FinalResponseDto<NotificationResponseDto>> getNotificationDetail(@PathVariable long notificationId) {
        MemberEntity member = JwtUtil.getMember();
        FinalResponseDto<NotificationResponseDto> response = this.memberService.getNotificationDetail(member.getId(), notificationId);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete-notifications")
    public ResponseEntity<FinalResponseDto<String>> deleteNotifications(@RequestBody DeleteMemberNoticeReqeustDto dto) {
        FinalResponseDto<String> response = this.memberService.deleteNotifications(dto);
        return ResponseEntity.ok(response);
    }
}
//TODO: 자리 점유 요청
//회원 회원가입 , 로그인,회원정보 가져오기 , 회원탈퇴요청,로그아웃
