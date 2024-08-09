package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class ProductResponseDto {
    private Long productId;
    private String name;
    private int amount;
    private int period;
    private String type;
}
