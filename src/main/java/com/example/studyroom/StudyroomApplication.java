//Spring Boot 애플리케이션의 진입점입니다.
package com.example.studyroom;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication//스프링 부트 애플리케이션의 메인 클래스임을 나타냅니다.
public class StudyroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyroomApplication.class, args);
    }//스프링 부트 애플리케이션을 실행하는 메서드입니다.

}
