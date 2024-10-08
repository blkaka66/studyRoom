package com.example.studyroom.dto.requestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailVerifciationRequestDto {
    private String email;
    private String code;
}
