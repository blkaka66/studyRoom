package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.RemainTimeResponseDto;
import com.example.studyroom.model.*;
import com.example.studyroom.repository.EnterHistoryRepository;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.TicketHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MemberServiceImpl extends BaseServiceImpl<MemberEntity> implements MemberService {
    private final MemberRepository repository;
    private final EnterHistoryRepository enterHistoryRepository;

    private final TicketHistoryRepository ticketHistoryRepository;

    public MemberServiceImpl(MemberRepository repository, EnterHistoryRepository enterHistoryRepository, TicketHistoryRepository ticketHistoryRepository) {
        super(repository);
        this.repository = repository;
        this.enterHistoryRepository = enterHistoryRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
    }


    @Override
    public List<MemberEntity> findByShop(ShopEntity shop) {
        return repository.findByShop(shop);
    }


    //자리선택시 enterhistory enterTime까지 생성하는 메서드 만들어야함
    @Override //로그인
    public MemberEntity login(String phone, String password) {
        //레포지토리에있는 함수가져오기
        MemberEntity Member = repository.findByPhoneAndPassword(phone, password);

        if (Member != null) {
            // 회원 존재하면 로그인 성공

            return Member;
        } else {
            throw new RuntimeException("로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
    }



    @Override
    public RemainTimeResponseDto getRemainTime(Long shopId, Long userId) {
        //id로 티켓히스토리 엔티티 정보 가져오기
        // A -> 만료, A` -> 현재 사용 가능한....
        // TODO: 만료된것 제외하고 가져오기.
        TicketHistoryEntity ticketHistory = ticketHistoryRepository.findByShopIdAndUserId(shopId, userId);

        if (ticketHistory != null) {//만약 ticketPaymentOpt이 존재한다면(티켓을 산적이 있다면)
            TicketEntity ticket = ticketHistory.getTicket();
            String ticketCategory = ticket.getType();
            String ticketExpireTime;

            if ("시간권".equals(ticketCategory)) {//그 티켓이 시간권이면
                // 시간권인 경우 remainTime을 Duration 형식으로 가져와 문자열로 변환.
                Duration remainTime = ticketHistory.getRemainTime();
                ticketExpireTime = remainTime.toHours() + "시간";
            } else if ("기간권".equals(ticketCategory)) {//그 티켓이 기간권이면
                // 기간권인 경우 endDate를 LocalDateTime 형식으로 가져와 포맷팅.
                ticketExpireTime = ticketHistory.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } else {
                throw new IllegalArgumentException("Unknown ticket category: " + ticketCategory);
            }

            // DTO로 바꿔서 반환.
            return RemainTimeResponseDto.builder()
                    .ticketCategory(ticketCategory)
                    .ticketExpireTime(ticketExpireTime)
                    .build();
        } else {
            // TicketPaymentEntity가 존재하지 않을 경우 예외를 던집니다.
            throw new RuntimeException("Ticket payment not found for shopId: " + shopId + ", userId: " + userId);
        }
    }

    @Override
    // TODO: Get Seat ID... 현재 유저가 자리하는 곳 반환
    public EnterHistoryEntity getSeatId(Long userId) {
        return null;
    }


    // 회원 ID를 받아 해당 회원의 좌석 ID를 반환하는 메서드
    // 조건에 맞는 EnterHistoryEntity가 없으면 null을 반환
    public Long getSeatIdByCustomerId(Long customerId) {
        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(customerId);
        if (enterHistory != null) {
            return enterHistory.getSeatId();  // 좌석 ID를 반환
        }
        return null;  // 조건에 맞는 기록이 없으면 null 반환
    }

}
