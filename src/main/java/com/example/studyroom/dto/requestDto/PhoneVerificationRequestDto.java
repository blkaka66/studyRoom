package com.example.studyroom.dto.requestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PhoneVerificationRequestDto {
    private String phoneNumber;
    private String code;
}
