package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.EmailRequestDto;
import com.example.studyroom.dto.requestDto.EmailVerifciationRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;


public interface EmailService {

    //인증코드 전송
    FinalResponseDto<String> sendVerifyCode(EmailRequestDto emailDto);

    //코드인증
    FinalResponseDto<String> verifingCode(EmailVerifciationRequestDto dto);
}
