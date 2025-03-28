//package com.example.studyroom.event;
//
//import com.example.studyroom.service.ChatService;
//import com.example.studyroom.service.ChatServiceImpl;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class WebSocketDisconnectHandler {
//
//    private final ChatServiceImpl chatServiceImpl;
//
//    @EventListener
//    public void handleDisconnect(SessionDisconnectEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String sessionId = accessor.getSessionId();
//        String userId = (String) accessor.getSessionAttributes().get("userId");
//        String userType = (String) accessor.getSessionAttributes().get("userType");
//
//        log.info("🔌 WebSocket 연결 해제됨 - sessionId: {}, userId: {}, userType: {}", sessionId, userId, userType);
//
//        // 📝 Redis에 있는 메시지를 DB에 바로 저장
//        chatServiceImpl.saveMessagesToDB();  // ❗주의: 이 메서드는 동기 저장이므로 성능 주의
//    }
//}
