package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.EmailRequestDto;
import com.example.studyroom.dto.requestDto.EmailVerifciationRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }


    @PostMapping("/send")
    public ResponseEntity<FinalResponseDto<String>> mailSend(@RequestBody EmailRequestDto dto) {

        return ResponseEntity.ok(emailService.sendVerifyCode(dto));

    }

    @PostMapping("/verification")
    public ResponseEntity<FinalResponseDto<String>> verifingCode(@RequestBody EmailVerifciationRequestDto dto) {

        return ResponseEntity.ok(emailService.verifingCode(dto));

    }

}
