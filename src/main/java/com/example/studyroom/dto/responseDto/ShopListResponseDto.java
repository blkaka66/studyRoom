package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.ShopEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ShopListResponseDto {
    private Long shopId;
    private String name;

    public static ShopListResponseDto of(ShopEntity shop) {
                return ShopListResponseDto.builder()
                .shopId(shop.getId())
                .name(shop.getName())
                .build();
    }

    public static List<ShopListResponseDto> of(List<ShopEntity> shopList) {
        return shopList.stream().map(ShopListResponseDto::of)
                .collect(Collectors.toList());
    }
}
