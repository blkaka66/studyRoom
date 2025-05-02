package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.PhoneVerificationRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<FinalResponseDto<String>> sendSms(@RequestBody PhoneVerificationRequestDto dto) {
        return ResponseEntity.ok(smsService.sendSms(dto));
    }
}
