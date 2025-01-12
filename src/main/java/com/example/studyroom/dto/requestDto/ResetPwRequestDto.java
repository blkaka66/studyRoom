package com.example.studyroom.dto.requestDto;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class ResetPwRequestDto {
    @NotNull(message = "비밀번호는 필수입니다.")
    private String password; // 비밀번호

    @NotNull(message = "새로운 비밀번호는 필수입니다.")
    private String newPassword; // 바꿀 새로운 비밀번호
}
