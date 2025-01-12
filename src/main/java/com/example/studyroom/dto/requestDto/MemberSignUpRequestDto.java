package com.example.studyroom.dto.requestDto;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSignUpRequestDto {
    @NotNull(message = "상점 ID는 필수입니다.")
    private Long shopId; // 상점 ID (ShopEntity와의 관계)

    @NotNull(message = "전화번호는 필수입니다.")
    private String phone; // 전화번호

    @NotNull(message = "이름은 필수입니다.")
    private String name; // 이름

    @NotNull(message = "비밀번호는 필수입니다.")
    private String password; // 비밀번호

    public MemberEntity toEntity() {
        return MemberEntity.builder()
                .phone(this.phone)
                .name(this.name)
                .password(this.password)
                .build();
    }
}
