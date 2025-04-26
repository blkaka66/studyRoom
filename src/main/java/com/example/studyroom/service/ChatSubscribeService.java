package com.example.studyroom.service;

public interface ChatSubscribeService {

    void subscribe(String userType, Long userId, Long roomId);

    void unsubscribe(String userType, Long userId, Long roomId);

    boolean isSubscribed(String userType, Long userId, Long roomId);

    void unsubscribeAll(String userType, Long userId);
}
