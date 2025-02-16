package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.statistics.SeatIdUsageEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SeatIdUsageResponseDto {

    private List<SeatIdUsageEntity> seatIdUsageEntityList;


}
