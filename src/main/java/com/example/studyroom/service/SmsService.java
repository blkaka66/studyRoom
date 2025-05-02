package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.PhoneVerificationRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;

public interface SmsService {
    FinalResponseDto<String> sendSms(PhoneVerificationRequestDto dto);
}
