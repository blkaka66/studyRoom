package com.example.studyroom.dto;

import com.example.studyroom.model.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MemberResponseDto {
    private String phone;
    private String name;

    @Builder
    public MemberResponseDto(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    public static List<MemberResponseDto> converter(List<MemberEntity> memberEntities) {
        return memberEntities.stream()
                .map( x -> MemberResponseDto.builder()
                        .name(x.getName())
                        .phone(x.getPhone())
                        .build()
                )
                .collect(Collectors.toList());
    }
}