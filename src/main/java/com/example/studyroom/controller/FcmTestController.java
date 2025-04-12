package com.example.studyroom.controller;

import com.example.studyroom.service.FcmTokenServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm-test")
public class FcmTestController {

    private final FcmTokenServiceImpl fcmTokenService;

    @PostMapping
    public void sendTest(@RequestParam Long requesterId, @RequestParam String requesterType) {
        fcmTokenService.sendTestNotification(requesterId, requesterType);
    }
}
