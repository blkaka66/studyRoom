package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MemberResponseDto {
    private long userId;
    private String phoneNumber;
    private String name;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastEnterTime;

    @Builder
    public MemberResponseDto(String phone, String name, long userId, OffsetDateTime createdAt, OffsetDateTime lastEnterTime) {
        this.phoneNumber = phone;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
        this.lastEnterTime = lastEnterTime;
    }

    public static List<MemberResponseDto> of(List<MemberEntity> memberEntities) {
        return memberEntities.stream()
                .map(member -> MemberResponseDto.builder()
                        .name(member.getName())
                        .phone(member.getPhone())
                        .userId(member.getId())
                        .createdAt(member.getCreatedAt())
                        .lastEnterTime(member.getLastEnterTime())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
