package com.example.studyroom.dto.requestDto;

import com.example.studyroom.model.ShopEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
//점주 회원가입
//나중에 builder로 이런식으로 만들수있다.

public class ShopSignUpRequestDto {

    private String location;
    private String name;
    private String email;
    private String password;


    public ShopEntity toEntity() { //ShopSignUpRequestDto 이형태를 shopentity로 바꾸는 기능(근데이게 여기있어야하나?)
        return ShopEntity.builder()
                .location(this.location)
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .build();
    }


}
