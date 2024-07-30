package com.example.studyroom.dto.responseDto;

import java.awt.*;

import com.example.studyroom.model.ShopEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShopInfoResponseDto {
    private String location;
    private String name;

}
