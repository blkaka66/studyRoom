package com.example.studyroom.dto.requestDto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopPayRequestDto {
    private Long productId;
    private String category;
}
