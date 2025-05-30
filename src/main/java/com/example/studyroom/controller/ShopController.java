package com.example.studyroom.controller;

import com.example.studyroom.dto.CookieDto;
import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.EnterHistoryEntity;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.statistics.SeatIdUsageEntity;
import com.example.studyroom.security.JwtCookieUtil;
import com.example.studyroom.security.JwtUtil;
import com.example.studyroom.service.NoticeService;
import com.example.studyroom.service.ShopService;
import com.example.studyroom.service.TokenRefreshService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    private final TokenRefreshService tokenRefreshService;
    private final NoticeService noticeService;

    public ShopController(ShopService shopService, TokenRefreshService tokenRefreshService, NoticeService noticeService
    ) {
        this.shopService = shopService;
//        this.memberService = memberService;
        this.tokenRefreshService = tokenRefreshService;
        this.noticeService = noticeService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<FinalResponseDto<ShopEntity>> signUp(@RequestBody ShopSignUpRequestDto shop) {
        // signUp 로직을 구현한 후, 성공 메시지와 함께 생성된 ShopEntity를 반환
        FinalResponseDto<ShopEntity> createdShop = shopService.signUp(shop);
        return ResponseEntity.ok(createdShop);

//        return ResponseEntity.ok(
//                FinalResponseDto.<ShopEntity>builder()
//                        .message("상점이 성공적으로 등록되었습니다.")
//                        .statusCode("0000")
//                        .data(createdShop)
//                        .build()
//        );
    }

    @PostMapping("/login")
    public ResponseEntity<FinalResponseDto<String>> login(@RequestBody ShopSignInRequestDto shop, HttpServletResponse response) {
        FinalResponseDto<String> token = shopService.login(shop, response);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logOut")
    public ResponseEntity<FinalResponseDto<String>> logout(@RequestHeader("Authorization") String authorizationHeader) {

        ShopEntity shop = JwtUtil.getShop();
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7); // "Bearer "를 제외한 토큰만 추출
        }
        FinalResponseDto<String> response = this.shopService.logout(shop, accessToken);
        return ResponseEntity.ok(response);
    }


    private static final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @GetMapping("/sign-in/shop-list")
    public ResponseEntity<FinalResponseDto<List<ShopListResponseDto>>> getShopList() {
        logger.info("getShopList 호출됨");
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

    @GetMapping("/member-list/{shop_id}")
    public ResponseEntity<FinalResponseDto<List<MemberResponseDto>>> memberList(@PathVariable("shop_id") Long shopId) {

        return ResponseEntity.ok(this.shopService.getMemberListAndInfo(shopId));
    }

    @PostMapping("/member/force-delete")
    public ResponseEntity<FinalResponseDto<String>> forceDeleteUser(@RequestBody ForceDeleteUserRequestDto dto) {
        return ResponseEntity.ok(this.shopService.forceDeleteUser(dto));
    }

    @GetMapping("/seat-usage/{shop_id}")
    public ResponseEntity<FinalResponseDto<List<SeatUsageStatsResponseDto>>> seatUsageStats(@PathVariable("shop_id") Long shopId) {
        return ResponseEntity.ok(this.shopService.getSeatUsageStats(shopId));
    }

//    @GetMapping("/member-list")
//    public ResponseEntity<FinalResponseDto<List<MemberResponseDto>>> memberList() {
//        List<MemberResponseDto> memberDtos = MemberResponseDto.of(
//                this.memberService.findAll()
//        );
//        // TODO: of 사용
//        return ResponseEntity.ok(
//                FinalResponseDto.<List<MemberResponseDto>>builder()
//                        .message("모든 회원 목록을 성공적으로 가져왔습니다.")
//                        .statusCode("0000")
//                        .data(memberDtos)
//                        .build()
//        );
//    }

    @GetMapping("/{shop_id}")
    public ResponseEntity<FinalResponseDto<ShopInfoResponseDto>> getShopInfo(@PathVariable("shop_id") Long shopId) {
        FinalResponseDto<ShopInfoResponseDto> shopInfo = this.shopService.getShopInfo(shopId);

        return ResponseEntity.ok(shopInfo);
    }

    @GetMapping("/getRoomAndSeat")
    public ResponseEntity<FinalResponseDto<List<RoomAndSeatInfoResponseDto>>> getRoomsAndSeatsByShopId(HttpServletResponse response) {
        MemberEntity member = JwtUtil.getMember();
        System.out.println("Member: " + member.getId());
//        System.out.println("GET /getRoomAndSeat/{id} 요청이 들어옴. id=" + id);

        // for Test
        response.addHeader("Set-Cookie", "myCookie=cookieValue; Path=/; HttpOnly;");


        // TODO: 수정 필요
        return ResponseEntity.ok(
                this.shopService.getRoomsAndSeatsByShopId(member.getId())
        );
    }


    @GetMapping("/{shopId}/getProductInfo")
    public ResponseEntity<FinalResponseDto<ProductResponseDto>> getProductListByShopId(@PathVariable("shopId") Long shopId) {
//        FinalResponseDto<List<ProductResponseDto>> productList = this.shopService.getProductList(shopId, type);

        return ResponseEntity.ok(this.shopService.getProductList(shopId));
    }


    @PostMapping("/create/announcement")
    public ResponseEntity<FinalResponseDto<String>> createAnnounement(@RequestBody CreateAnnouncementRequestDto dto) {

        Long id = JwtUtil.getMember().getShop().getId();

        return ResponseEntity.ok(this.shopService.createAnnounement(id, dto));
    }


    @GetMapping("/announcement-list/{id}")
    public ResponseEntity<FinalResponseDto<List<AnnouncementResponseDto>>> getAnnouncementList(@PathVariable("id") Long shopId) {
        return ResponseEntity.ok(this.shopService.getAnnouncementList(shopId));
    }

    @GetMapping("/announcement/{id}")
    public ResponseEntity<FinalResponseDto<AnnouncementResponseDto>> getAnnouncementInfo(@PathVariable("id") Long docsId) {
        return ResponseEntity.ok(this.shopService.getAnnouncementInfo(docsId));
    }


    @GetMapping("/couponInfo/{couponCode}")
    public ResponseEntity<FinalResponseDto<CouponInfoResponseDto>> getCouponInfo(@PathVariable("couponCode") String couponCode) {
        Long id = JwtUtil.getMember().getShop().getId();
        return ResponseEntity.ok(this.shopService.getCouponInfo(couponCode, id));
    }

    @PostMapping("/statistics/seatUsage")
    public ResponseEntity<FinalResponseDto<SeatIdUsageResponseDto>> getSeatUsageEntitiesByDateRange(@RequestBody SeatIdUsageRequestDto dto) {
        return ResponseEntity.ok(this.shopService.getSeatUsageEntitiesByDateRange(dto));
    }

    @PostMapping("/statistics/payment")
    public ResponseEntity<FinalResponseDto<List<ShopDailyPaymentResponseDto>>> getShopPaymentByDateRange(@RequestBody ShopPaymentRequestDto dto) {
        return ResponseEntity.ok(this.shopService.getShopDailyPaymentsByDateRange(dto));
    }

    @PostMapping("/statistics/payment-by-name")
    public ResponseEntity<FinalResponseDto<PaymentHistoryDto>> getShopPaymentByDateRangeAndByName(@RequestBody ShopPaymentRequestIncludeNameDto dto) {
        return ResponseEntity.ok(this.shopService.getShopDailyPaymentsByDateRangeAndByName(dto));
    }

    @PostMapping("/statistics/shopUsage")
    public ResponseEntity<FinalResponseDto<List<ShopUsageResponseDto>>> getShopUsageByDateRange(@RequestBody ShopUsageRequestDto dto) {
        return ResponseEntity.ok(this.shopService.getShopUsageByDateRange(dto));
    }


    @PostMapping("/statistics/userAvrUsage")
    public ResponseEntity<FinalResponseDto<List<UserAvrUsageResponseDto>>> getUserAvrUsageByDateRange(@RequestBody UserAvrUsageRequestDto dto) {
        return ResponseEntity.ok(this.shopService.getUserAvrUsageByDateRange(dto));
    }

    @PostMapping("/statistics/userChangeStats")
    public ResponseEntity<FinalResponseDto<List<UserChangeStatsResponseDto>>> getUserChangeStatsByDateRange(@RequestBody UserChangeStatsRequestDto dto) {
        return ResponseEntity.ok(this.shopService.getUserChangeStatsByDateRange(dto));
    }


    @GetMapping("/get-notifications")
    public ResponseEntity<FinalResponseDto<List<NotificationResponseDto>>> getNotifications() {
        ShopEntity shop = JwtUtil.getShop();
        FinalResponseDto<List<NotificationResponseDto>> response = this.noticeService.getNotifications(shop.getId());
        return ResponseEntity.ok(response);
    }


}



