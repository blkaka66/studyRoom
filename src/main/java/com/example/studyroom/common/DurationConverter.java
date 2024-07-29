package com.example.studyroom.common; // 클래스가 속한 패키지 선언

import jakarta.persistence.AttributeConverter; // JPA AttributeConverter 인터페이스 임포트
import jakarta.persistence.Converter; // JPA Converter 애노테이션 임포트
import java.time.Duration; // Java 시간 클래스 Duration 임포트

@Converter(autoApply = true) // 이 클래스가 모든 Duration 타입 필드에 자동으로 적용되도록 설정
public class DurationConverter implements AttributeConverter<Duration, String> {

    @Override
    public String convertToDatabaseColumn(Duration attribute) {
        // 엔티티의 Duration 속성을 데이터베이스 컬럼의 String 타입으로 변환
        return attribute == null ? null : attribute.toString(); // Duration 객체를 문자열로 변환, null 처리 포함
    }

    @Override
    public Duration convertToEntityAttribute(String dbData) {
        // 데이터베이스의 String 컬럼을 엔티티의 Duration 속성으로 변환
        return dbData == null ? null : Duration.parse(dbData); // 문자열을 Duration 객체로 변환, null 처리 포함
    }
}
