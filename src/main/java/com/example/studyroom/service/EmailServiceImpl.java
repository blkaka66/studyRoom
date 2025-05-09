package com.example.studyroom.service;


import com.example.studyroom.dto.requestDto.EmailRequestDto;
import com.example.studyroom.dto.requestDto.EmailVerifciationRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.type.ApiResult;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisService redisService;
    private final TemplateEngine templateEngine;

    @Value("${spring.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailServiceImpl(RedisService redisService, JavaMailSender javaMailSender) {

        this.javaMailSender = javaMailSender;
        this.redisService = redisService;

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(resolver);
    }


    //인증코드만들기
    private String createCode() {
        int leftLimit = 48; // number '0'
        int rightLimit = 122; // alphabet 'z'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    // 이메일 내용 초기화
    private String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("mail", context);
    }

    private String makeKey(String email) {
        return "email:" + email;
    }

    // 이메일 폼 생성
    private MimeMessage createEmailForm(String email, String authCode) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("안녕하세요. 인증번호입니다.");
            message.setFrom(senderEmail);

            message.setText(setContext(authCode), "utf-8", "html");
            return message;
        } catch (Exception e) {
            log.error("이메일 폼 생성 중 오류 발생", e);
            throw new RuntimeException("이메일 생성 실패", e);
        }
    }

    public FinalResponseDto<String> sendVerifyCode(EmailRequestDto emailDto) {
        String key = makeKey(emailDto.getMail());
        String authCode = createCode();
        log.info("key: {}", key);

        if (redisService.isKeyPresent(key)) {
            log.info("중복키 발생");
            redisService.deleteByKey(key);
        }

        // 이메일 폼 생성
        MimeMessage emailForm = createEmailForm(emailDto.getMail(), authCode);

        // 이메일 발송
        javaMailSender.send(emailForm);

        // Redis 에 해당 인증코드 인증 시간 설정
        redisService.setValuesWithTTL(key, authCode, authCodeExpirationMillis / 1000);

        return FinalResponseDto.success();
    }

    public FinalResponseDto<String> verifingCode(EmailVerifciationRequestDto dto) {
        String key = makeKey(dto.getEmail());
        log.info("key: {}", key);
        if (!redisService.isKeyPresent(key)) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        log.info("value: {} , code: {}", redisService.getValues(key), dto.getCode());
        if (!Objects.equals(redisService.getValues(key), dto.getCode())) {
            return FinalResponseDto.failure(ApiResult.AUTHENTICATION_FAILED);
        }
        return FinalResponseDto.success();
    }


}
