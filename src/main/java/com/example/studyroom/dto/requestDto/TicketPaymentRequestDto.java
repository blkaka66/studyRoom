package com.example.studyroom.dto.requestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TicketPaymentRequestDto { //근데 이런 한줄짜리도 굳이 따로 만들어야하나?
    private Long productId;
}
