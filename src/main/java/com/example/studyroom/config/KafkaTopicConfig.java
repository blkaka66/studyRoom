package com.example.studyroom.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic chatMessagesTopic() {
        return TopicBuilder.name("chat-messages")
                .partitions(1)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic chatEventsTopic() {
        return TopicBuilder.name("chat-events")
                .partitions(1)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic dltChatMessagesTopic() {
        return TopicBuilder.name("chat-messages.DLT")
                .partitions(1)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic memberSignUpTopic() {
        return TopicBuilder.name("member-signup-notice")
                .partitions(1)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic seatAlertTopic() {
        return TopicBuilder.name("seat-expiration-warning")
                .partitions(1)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic ticketAlertTopic() {
        return TopicBuilder.name("ticket-expiration-warning")
                .partitions(1)
                .replicas(2)
                .build();
    }

}
