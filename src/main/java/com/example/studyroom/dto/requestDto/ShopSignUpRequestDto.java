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
//ShopSignUpRequestDto dto = ShopSignUpRequestDto.builder()
//        .location("Seoul")
//        .name("Example Shop")
//        .email("example@example.com")
//        .password("password123")
//        .isVerification(false)
//        .build();

public class ShopSignUpRequestDto {
    private String location;
    private String name;
    private String email;
    private String password;
    private Boolean isVerification;


    public ShopEntity toEntity() { //ShopSignUpRequestDto 이형태를 shopentity로 바꾸는 기능(근데이게 여기있어야하나?)
        return ShopEntity.builder()
                .location(this.location)
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .build();
    }

//    @Builder
//    public ShopSignUpRequestDto(String location, String name,String email,String password,Boolean isVerification) {
//        this.location = location;
//        this.name = name;
//        this.email = email;
//        this.password = password;
//        this.isVerification = isVerification;
//    }
}
