//package com.example.studyroom.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class WebSocketServiceImpl implements WebSocketService {
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    // 클라이언트에게 알림을 전송하는 구현
//    @Override
//    public void sendNotification(Long userId, String message) {
//        // "/topic/notifications" 채널로 메시지를 전송
//        messagingTemplate.convertAndSend("/topic/notifications/" + userId, message);
//    }
//}
