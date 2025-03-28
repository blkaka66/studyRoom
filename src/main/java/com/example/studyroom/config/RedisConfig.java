//package com.example.studyroom.config;
//
//import com.example.studyroom.service.RedisChatSubscriber;
//import com.example.studyroom.service.RedisExpirationListener;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.PatternTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//
//@Configuration
//public class RedisConfig {
//    private final RedisChatSubscriber redisChatSubscriber;
//    private final RedisExpirationListener redisExpirationListener;
//
//    // ✅ 생성자 주입 (Lombok 사용 안 함)
//    public RedisConfig(RedisChatSubscriber redisChatSubscriber, RedisExpirationListener redisExpirationListener) {
//        this.redisChatSubscriber = redisChatSubscriber;
//        this.redisExpirationListener = redisExpirationListener;
//    }
//
//    /**
//     * ✅ Redis Pub/Sub (채팅 메시지 주고받기)용 채널 설정
//     */
//    @Bean
//    public ChannelTopic chatTopic() {
//        return new ChannelTopic("chatroom");
//    }
//
//    @Bean
//    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@0__:expired"));
//
//
//        // ✅ Redis Pub/Sub을 통한 채팅 메시지 구독
//        container.addMessageListener(new MessageListenerAdapter(redisChatSubscriber), chatTopic());
//
//
//        return container;
//    }
//
//    /**
//     * RedisTemplate 설정 (Redis 연동)
//     */
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//        return template;
//    }
//
//    @Bean
//    MessageListenerAdapter listenerAdapter(RedisExpirationListener expirationListener) {
//        return new MessageListenerAdapter(expirationListener);
//    }
//
//
//}
