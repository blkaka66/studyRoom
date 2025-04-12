package com.example.studyroom.service;

public interface ChatSubscribeService {
    
    void subscribe(Long userId, Long roomId);

    void unsubscribe(Long userId, Long roomId);

    boolean isSubscribed(Long userId, Long roomId);
}
