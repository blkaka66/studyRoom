package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class TicketController {

    @PostMapping("/pay")
    public FinalResponseDto<String> processPayment(@RequestBody ShopPayRequestDto product) {
//        FinalResponseDto<List<ProductResponseDto>> productList = this.shopService.getProductList(shopId, type);
        // TODO: Pay Service 호출 필요
//        return ResponseEntity.ok(this.shopService.getProductList(SecurityUtil.getShopInfo().getId(), type));

//        ShopEntity shop = SecurityUtil.getShopInfo();
//        System.out.println(shop);
        return FinalResponseDto.success();
    }
}
//티켓, 맨밑에결제내역
