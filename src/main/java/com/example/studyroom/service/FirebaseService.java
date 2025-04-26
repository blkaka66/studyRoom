package com.example.studyroom.service;

import java.util.Map;

public interface FirebaseService {
    void sendMessageToToken(String fcmToken, String title, String body, Map<String, String> data);
}
