package com.example.studyroom.dto.requestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class MemberSignInRequestDto {
    private String phoneNumber;
    private String password;
    private int storeId;
}
