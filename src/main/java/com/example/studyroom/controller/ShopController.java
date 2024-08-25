package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.ShopSignInRequestDto;
import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.service.MemberService;
import com.example.studyroom.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;
//    private final MemberService memberService;

    public ShopController(ShopService shopService
//                          MemberService memberService
    ) {
        this.shopService = shopService;
//        this.memberService = memberService;
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
    public ResponseEntity<FinalResponseDto<String>> login(@RequestBody ShopSignInRequestDto shop) {
        FinalResponseDto<String> token = shopService.login(shop);
        return ResponseEntity.ok(token);
    }


//    @GetMapping()
//    public ResponseEntity<FinalResponseDto<List<ShopListResponseDto>>> getShopList() {
//        List<ShopEntity> shops = shopService.getShopList();
//        List<ShopListResponseDto> shopDtos = ShopListResponseDto.of(shops);
//        return ResponseEntity.ok(
//                FinalResponseDto.<List<ShopListResponseDto>>builder()
//                        .message("상점 목록을 성공적으로 가져왔습니다.")
//                        .statusCode("0000")
//                        .data(shopDtos)
//                        .build()
//        );
//    }

    @GetMapping("/member-list/{shop_id}")
    public ResponseEntity<FinalResponseDto<List<MemberResponseDto>>> memberList(@PathVariable("shop_id") Long shopId) {

        // TODO: MemberResponseDto.of 관련 제거 (서비스 안쪽으로 로직 이동)
//        FinalResponseDto<List<MemberResponseDto>> memberDtos = this.shopService.getMemberList(shopId);
//
//        return ResponseEntity.ok(memberDtos);

        return ResponseEntity.ok(this.shopService.getMemberList(shopId));
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

//        // TODO: 아래 내용 수정 필요
//        return ResponseEntity.ok(
//                FinalResponseDto.<ShopInfoResponseDto>builder()
//                        .message("상점 정보를 성공적으로 가져왔습니다.")
//                        .statusCode("0000")
//                        .data(shopInfo)
//                        .build()
//        );
        return ResponseEntity.ok(shopInfo);
    }

//    @GetMapping("/{shopId}/room")
//    public ResponseEntity<FinalResponseDto<List<RoomAndSeatInfoResponseDto>>> getRoomsAndSeatsByShopId(@PathVariable("shopId") Long shopId) {
//        // TODO: 쿠키에서 customerId 추출하는 메서드 추가 (토큰에서..)
//        List<RoomAndSeatInfoResponseDto> roomAndSeatInfo = this.shopService.getRoomsAndSeatsByShopId(shopId, customerId);
//
//        // TODO: 수정 필요
//        return ResponseEntity.ok(
//                FinalResponseDto.<List<RoomAndSeatInfoResponseDto>>builder()
//                        .message("방과 좌석 정보를 성공적으로 가져왔습니다.")
//                        .statusCode("0000")
//                        .data(roomAndSeatInfo)
//                        .build()
//        );
//    }

    @GetMapping("/{shopId}/{productType}")
    public ResponseEntity<FinalResponseDto<List<ProductResponseDto>>> getProductListByShopId(@PathVariable("shopId") Long shopId, @PathVariable("productType") String type) {
//        FinalResponseDto<List<ProductResponseDto>> productList = this.shopService.getProductList(shopId, type);
        // TODO: 수정 완료
        return ResponseEntity.ok(this.shopService.getProductList(shopId, type));
    }


}


/*package com.example.studyroom.controller;

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
*/
