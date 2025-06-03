//Spring Boot 애플리케이션의 진입점입니다.
package com.example.studyroom;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.core.env.Environment;

@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication//스프링 부트 애플리케이션의 메인 클래스임을 나타냅니다.
public class StudyroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyroomApplication.class, args);
    }

}
