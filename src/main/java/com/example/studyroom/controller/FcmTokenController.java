package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.FcmTokenRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fcm-token")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;
    public FcmTokenController( FcmTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }
    @PostMapping
    public FinalResponseDto<String> registerFcmToken(@RequestBody FcmTokenRequestDto dto) {
        return fcmTokenService.saveToken(dto.getRequesterId(), dto.getRequesterType(), dto.getToken());

    }
}
