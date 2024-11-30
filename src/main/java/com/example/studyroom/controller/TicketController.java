package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.TimeTicketEntity;
import com.example.studyroom.repository.TimeTicketRepository;
import com.example.studyroom.security.SecurityUtil;
import com.example.studyroom.service.PeriodTicketService;
import com.example.studyroom.service.TimeTicketService;
import com.example.studyroom.type.ApiResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/ticket")

public class TicketController {

    private final TimeTicketService timeTicketService;
    private final PeriodTicketService periodTicketService;

    private final TimeTicketRepository timeTicketRepository;

    public TicketController(TimeTicketService timeTicketService, PeriodTicketService periodTicketService, TimeTicketRepository timeTicketRepository) {
        this.timeTicketService = timeTicketService;
        this.periodTicketService = periodTicketService;
        this.timeTicketRepository = timeTicketRepository;
    }


    @PostMapping("/pay")
    public FinalResponseDto<String> processPayment(@RequestBody ShopPayRequestDto product) {
        MemberEntity member = SecurityUtil.getMemberInfo();
        System.out.println("안녕하세요"+member.getName());
//        TimeTicketEntity ticket = new TimeTicketEntity();
//        ShopEntity shop = new ShopEntity();
//        shop.setId(1L);
//        ticket.setShop(shop);
//        ticket.setName("1시간권");
//        ticket.setAmount(1000);
//        ticket.setPeriod(Duration.ofHours(1));
//
//        timeTicketRepository.saveAndFlush(ticket);
        System.out.println("카테고리"+product.getCategory());
        switch(product.getCategory()) {
            case "기간권":
                System.out.println("기간권!!");
                return periodTicketService.processPayment(product, member.getShop().getId(), member.getId());
            case "시간권":
                System.out.println("시간권!!");
                return timeTicketService.processPayment(product, member.getShop().getId(), member.getId());
        }
        return FinalResponseDto.failure(ApiResult.FAIL);
//        return FinalResponseDto.success();
    }
}
//티켓, 맨밑에결제내역
