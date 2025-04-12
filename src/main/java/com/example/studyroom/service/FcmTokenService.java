package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;

public interface FcmTokenService {
    FinalResponseDto<String> saveToken(Long requesterId, String requesterType, String token);

    void sendChatNotification(Long receiverId, String receiverType, String message);

}
