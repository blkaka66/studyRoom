package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.PhoneVerificationRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.type.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    private final RestTemplate restTemplate;

    @Value("${solapi.api.key}")
    private String apiKey;

    @Value("${solapi.api.secret}")
    private String apiSecret;

    @Value("${solapi.from.number}")
    private String fromNumber;

    public SmsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Hex.encodeHexString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public FinalResponseDto<String> sendSms(PhoneVerificationRequestDto dto) {
        try {
            String url = "https://api.solapi.com/messages/v4/send-many/detail";

            // 1) ISO8601 형식의 timestamp
            String date = OffsetDateTime.now()
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // 2) salt: 임의 랜덤 문자열 (12자 이상 권장)
            String salt = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

            // 3) signature: HMAC-SHA256(date + salt, apiSecret)
            String signature = hmacSha256(date + salt, apiSecret);

            // 4) Authorization 헤더 조립
            String authHeader = String.format(
                    "HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                    apiKey, date, salt, signature
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", authHeader);

            // 5) payload 구성
            Map<String, String> msg = new HashMap<>();
            msg.put("to", dto.getPhoneNumber());
            msg.put("from", fromNumber);
            msg.put("text", dto.getCode());
            msg.put("type", "SMS");

            Map<String, Object> payload = new HashMap<>();
            payload.put("messages", Collections.singletonList(msg));

            HttpEntity<Map<String, Object>> req = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);

            log.info("Solapi 응답: {}", resp.getBody());
            @SuppressWarnings("unchecked")
            List<?> failed = (List<?>) resp.getBody().get("failedMessageList");
            if (failed != null && !failed.isEmpty()) {
                return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
            }
            return FinalResponseDto.success();

        } catch (HttpClientErrorException.BadRequest bre) {
            log.error("잘못된 요청(400): {}", bre.getResponseBodyAsString());
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        } catch (Exception e) {
            log.error("SMS 전송 오류", e);
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
    }
}
