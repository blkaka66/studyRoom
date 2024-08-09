package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductListResponseDto {
    private List<ProductResponseDto> productInfo;
}
