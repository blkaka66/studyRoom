package com.example.studyroom.dto.requestDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class MemberSignInRequestDto {
    @NotNull(message = "phoneNumber 는 필수입니다.")
    private String phoneNumber;

    @NotNull(message = "password 는 필수입니다.")
    private String password;

    @NotNull(message = "상점 ID는 필수입니다.")
    private Long shopId;
}
