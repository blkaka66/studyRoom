package com.example.studyroom.dto.responseDto;
//이게 정확히 어떤상황에서의 dto였지?
import com.example.studyroom.model.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MemberResponseDto {
    private long userId;
    private String phoneNumber;
    private String name;

    @Builder
    public MemberResponseDto(String phone, String name , long userId) {
        this.phoneNumber = phone;
        this.name = name;
        this.userId = userId;
    }

    public static List<MemberResponseDto> of(List<MemberEntity> memberEntities) {
        return memberEntities.stream()
                .map( x -> MemberResponseDto.builder()
                        .name(x.getName())
                        .phone(x.getPhone())
                        .userId(x.getId())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
