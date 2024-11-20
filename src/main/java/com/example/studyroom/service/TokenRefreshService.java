package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface TokenRefreshService {
    FinalResponseDto<String> handleExpiredAccessToken(String token);
}
