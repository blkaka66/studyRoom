package com.example.studyroom.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEnvChecker {

    private final Environment env;

    @PostConstruct
    public void checkKafkaBootstrapServers() {
        log.info("bootstrap-servers={}", env.getProperty("spring.kafka.bootstrap-servers"));
    }
}
