package com.example.studyroom.controller;

import com.example.studyroom.common.ResultEntity;
import com.example.studyroom.dto.requestDto.OccupySeatRequestDto;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.service.MemberService;
import com.example.studyroom.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;
    private final MemberService memberService;

    public ShopController(ShopService shopService, MemberService memberService) {
        this.shopService = shopService;
        this.memberService = memberService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody ShopEntity shop) {
        return null;    // return statement is null.
    }

    @GetMapping()
    public ResponseEntity<List<ShopListResponseDto>> getShopList() {
        List<ShopEntity> shops = shopService.getShopList();
        return ResponseEntity.ok(ShopListResponseDto.of(shops));
    }




    @GetMapping("/member-list/{shop_id}")
    public ResponseEntity<List<MemberResponseDto>> memberList(@PathVariable("shop_id") Long shopId) {
        return ResponseEntity.ok(
                MemberResponseDto.of(
                        this.shopService.getMemberList(shopId)
                )
        );
    }

    @GetMapping("/member-list")
    public ResponseEntity<List<MemberResponseDto>> memberList() {
        return ResponseEntity.ok(
                MemberResponseDto.of(
                        this.memberService.findAll()
                )
        );
    }

    @GetMapping("/{shop_id}")
    public ResponseEntity<ShopInfoResponseDto> getShopInfo(@PathVariable("shop_id") Long shopId) {
        // 현재 로그인된 사용자의 지점 정보를 반환
        // 임시 데이터로 작성, 실제론 인증 정보를 바탕으로 데이터를 가져옴
//        return ResponseEntity.ok("{\"name\":\"동백역점\",\"location\":\"경기 용인시~\"}");
//        return

        return ResponseEntity.ok(
                this.shopService.getShopInfo(shopId)
        );
    }

    @GetMapping("/{shopId}/room")
    public RoomAndSeatInfoResponseDto getRoomsAndSeatsByShopId(@PathVariable("shop_id") Long shopId) {
        //TODO: 쿠키에서 customerId 추출하는 메서드추가
        return this.shopService.getRoomsAndSeatsByShopId(shopId, customerId);

    }

    @GetMapping("/{productType}")
    // TODO: Response Class 수정 필요
    public Object getRoomsAndSeatsByShopId(@PathVariable("productType") String type) {
        //TODO: 쿠키에서 shopId 추출하는 메서드추가
        return this.shopService.getProductList(shopId,type);

    }
}
