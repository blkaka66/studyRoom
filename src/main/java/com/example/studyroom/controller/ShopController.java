package com.example.studyroom.controller;

import com.example.studyroom.dto.responseDto.MemberResponseDto;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody ShopEntity shop) {

    }

    @GetMapping("/sign-in/shop-list")
    public ResponseEntity<List<ShopEntity>> getShopList() {
        List<ShopEntity> shops = shopService.findAll();
        return ResponseEntity.ok(shops);
    }

    // 이메일 발송 요청
    @PostMapping("/email/send")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        // 이메일 발송 로직 추가
        return ResponseEntity.ok("{\"message\": \"이메일이 발송 되었습니다. 이메일을 확인해 주세요.\", \"statusCode\": 200}");
    }

    // 이메일 인증코드 인증
    @PostMapping("/email/verification")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        // 이메일 인증 로직 추가
        return ResponseEntity.ok("{\"message\": \"인증이 완료 되었습니다.\", \"statusCode\": 200}");
    }


    @GetMapping("/member-list/{shop_id}")
    public ResponseEntity<List<MemberResponseDto>> memberList(@PathVariable("shop_id") Long shopId) {
        return ResponseEntity.ok(MemberResponseDto.converter(this.shopService.getMemberList(shopId)));
    }


    @GetMapping
    public ResponseEntity<?> getShopInfo() {
        // 현재 로그인된 사용자의 지점 정보를 반환
        // 임시 데이터로 작성, 실제론 인증 정보를 바탕으로 데이터를 가져옴
        return ResponseEntity.ok("{\"name\":\"동백역점\",\"location\":\"경기 용인시~\"}");
    }
}
