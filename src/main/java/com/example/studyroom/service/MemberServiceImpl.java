//package com.example.studyroom.service;
//
//import com.example.studyroom.dto.responseDto.FinalResponseDto;
//import com.example.studyroom.dto.responseDto.RemainTimeResponseDto;
//import com.example.studyroom.model.*;
//import com.example.studyroom.repository.*;
//
//import org.springframework.stereotype.Service;
//
//import java.lang.reflect.Member;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.Random;
//
//@Service
//public class MemberServiceImpl extends BaseServiceImpl<MemberEntity> implements MemberService {
//    private final MemberRepository repository;
//    private final EnterHistoryRepository enterHistoryRepository;
//
//    private final TicketHistoryRepository ticketHistoryRepository;
//    private final SeatRepository seatRepository;
//
//    private final ShopRepository shopRepository;
//    private final RoomRepository roomRepository;
//    public MemberServiceImpl(MemberRepository repository,
//                             EnterHistoryRepository enterHistoryRepository,
//                             SeatRepository seatRepository,
//                             TicketHistoryRepository ticketHistoryRepository,
//                             ShopRepository shopRepository,
//                             RoomRepository roomRepository) {
//        super(repository);
//        this.repository = repository;
//        this.enterHistoryRepository = enterHistoryRepository;
//        this.ticketHistoryRepository = ticketHistoryRepository;
//        this.seatRepository = seatRepository;
//        this.shopRepository = shopRepository;
//
//        this.roomRepository = roomRepository;
//    }
//
//
//    @Override
//    public List<MemberEntity> findByShop(ShopEntity shop) {
//        return repository.findByShop(shop);
//    }
//
//
//    //자리선택시 enterhistory enterTime까지 생성하는 메서드 만들어야함
//    @Override //로그인
//    public MemberEntity login(String phone, String password) {
//        //레포지토리에있는 함수가져오기
//        MemberEntity Member = repository.findByPhoneAndPassword(phone, password);
//
//        if (Member != null) {
//            // 회원 존재하면 로그인 성공
//
//            return Member;
//        } else {
//            throw new RuntimeException("로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.");
//        }
//    }
//
//
//
////
////    @Override
////    public RemainTimeResponseDto getRemainTime(Long shopId, Long userId) {
////        //id로 티켓히스토리 엔티티 정보 가져오기
////        // A -> 만료, A` -> 현재 사용 가능한....
////        // TODO: 만료된것 제외하고 가져오기.
////        //expired가 false것만 가져오
////        List<TicketHistoryEntity> ticketHistory = ticketHistoryRepository.findByShopIdAndUserIdAndExpiredFalse(shopId, userId); //만료안된것들만 가져오기(expired가 false)
////
////        if (ticketHistory != null) {//만약 ticketPaymentOpt이 존재한다면(티켓을 산적이 있다면)
////            TicketEntity ticket = ticketHistory.getTicket();
////            String ticketCategory = ticket.getType();
////            String ticketExpireTime;
////
////            if ("시간권".equals(ticketCategory)) {//그 티켓이 시간권이면
////                // 시간권인 경우 remainTime을 Duration 형식으로 가져와 문자열로 변환.
////                Duration remainTime = ticketHistory.getRemainTime();
////                totalRemainTime = totalRemainTime.plus(remainTime);
////            } else if ("기간권".equals(ticketCategory)) {//그 티켓이 기간권이면
////                // 기간권인 경우 endDate를 LocalDateTime 형식으로 가져와 포맷팅.
////                //Todo:기간권 남은시간을 어떻게 가져올지 모르겠다
////                ticketExpireTime = ticketHistory.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
////            } else {
////                throw new IllegalArgumentException("Unknown ticket category: " + ticketCategory);
////            }
////
////            // DTO로 바꿔서 반환.
////            return RemainTimeResponseDto.builder()
////                    .ticketCategory(ticketCategory)
////                    .ticketExpireTime(ticketExpireTime)
////                    .build();
////        } else {
////            // TicketPaymentEntity가 존재하지 않을 경우 예외를 던집니다.
////            throw new RuntimeException("Ticket payment not found for shopId: " + shopId + ", userId: " + userId);
////        }
////    }
//    @Override
//    public FinalResponseDto getRemainTime(Long shopId, Long userId) {
//        // 만료되지 않은 티켓 히스토리 엔티티 정보 가져오기
//        List<TicketHistoryEntity> ticketHistories = ticketHistoryRepository.findByShopIdAndUserIdAndExpiredFalse(shopId, userId);
//
//        if (!ticketHistories.isEmpty()) {
//            Duration totalRemainTime = Duration.ZERO;
//            LocalDateTime latestEndDate = null;
//
//            for (TicketHistoryEntity ticketHistory : ticketHistories) {
//                TicketEntity ticket = ticketHistory.getTicket();
//                String ticketCategory = ticket.getType();
//
//                if ("시간권".equals(ticketCategory)) {
//                    // 시간권인 경우 remainTime을 Duration 형식으로 가져와 누적
//                    Duration remainTime = ticketHistory.getRemainTime();
//                    totalRemainTime = totalRemainTime.plus(remainTime);
//                } else if ("기간권".equals(ticketCategory)) {
//                    //Todo:기간권 남은시간을 어떻게 가져올지 모르겠다. 현재 endtime은 현재시간+기간으로만 endtime을 지정하고있는데
//                    // 저장된 기간권의 tickethistory가 2개이상일 경우엔 어떡하지? 시간권처럼 남은기간을 현재에 더하고싶은데 방법이있나?
//                } else {
//                    throw new IllegalArgumentException("Unknown ticket category: " + ticketCategory);
//                }
//            }
//
//            // 시간권의 경우 총 남은 시간을 문자열로 변환
//            String ticketExpireTime = "";
//            if (totalRemainTime.toHours() > 0) {
//                ticketExpireTime = totalRemainTime.toHours() + "시간";
//            } else if (latestEndDate != null) {
//
//            } else {
//                // 만료 시간이 없는 경우 기본 메시지
//                ticketExpireTime = "No active ticket";
//            }
//
//            // DTO로 변환하여 반환
//            RemainTimeResponseDto remainTimeResponseDto= RemainTimeResponseDto.builder()
//                    .ticketCategory(ticketHistories.get(0).getTicket().getType()) // Assuming the ticket category is the same for all
//                    .ticketExpireTime(ticketExpireTime)
//                    .build();
//
//            return FinalResponseDto.builder()
//                    .message("정보가 성공적으로 반환되었습니다")
//                    .statusCode("0000")
//                    .data(remainTimeResponseDto)
//                    .build();
//
//
//        } else {
//            // 만료되지 않은 티켓이 존재하지 않을 경우 예외를 던집니다.
//            return FinalResponseDto.builder()
//                    .message("만료되지않은티켓존재")
//                    .statusCode("3000")
//                    .build();
//
//
//        }
//    }
//    @Override
//    // TODO: Get Seat ID... 현재 유저가 자리하는 곳 반환
//    public EnterHistoryEntity getSeatId(Long userId) {
//        return null;
//    }
//
//
//    // 회원 ID를 받아 해당 회원의 좌석 ID를 반환하는 메서드
//    // 조건에 맞는 EnterHistoryEntity가 없으면 null을 반환
//    public Long getSeatIdByCustomerId(Long customerId) {
//        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(customerId);
//        if (enterHistory != null) {
//            return enterHistory.getSeatId();  // 좌석 ID를 반환
//        }
//        return null;  // 조건에 맞는 기록이 없으면 null 반환
//    }
//
//
//    // TODO: Transactional 사용법 찾아보기!
//    @Override
//    public FinalResponseDto occupySeat(Long shopId , String roomName, int seatCode, Long memberId) {
//        Optional<ShopEntity> shopOpt = shopRepository.findById(shopId);//shop체크
//        if(shopOpt.isEmpty()) {
//            return FinalResponseDto.builder()
//                    .message("잘못된 샵정보")
//                    .statusCode("3000")
//                    .build();
//        }
//
//        Optional<RoomEntity> roomOpt = roomRepository.findByName(roomName);//룸체크
//        if(roomOpt.isEmpty()) {
//            return FinalResponseDto.builder()
//                    .message("잘못된 방정보")
//                    .statusCode("3000")
//                    .build();
//        }
//
//        Long roomId = roomOpt.get().getId();
//        Optional<SeatEntity> seatOpt = seatRepository.findBySeatCodeAndRoom_Id(seatCode, roomId);//자리체크
//        if(seatOpt.isEmpty()) {
//            return FinalResponseDto.builder()
//                    .message("주인있는 자리")
//                    .statusCode("3000")
//                    .build();
//        }
//
//        SeatEntity seat = seatOpt.get();
//        if(!seat.getAvailable()) {
//            return FinalResponseDto.builder()
//                    .message("자리가 차있음")
//                    .statusCode("3000")
//                    .build();
//        }
//        seat.setAvailable(false);
//        seatRepository.save(seat); //점유요청들어오면 seat abilable false로 바꾸기
//
//        Optional<MemberEntity> memberOpt = repository.findById(memberId);
//        Optional<TicketHistoryEntity> ticketHistoryOpt = Optional.ofNullable(ticketHistoryRepository.findByShopIdAndUserId(shopId, memberId));//티켓히스토리아이디 받아오기
//
//        if(memberOpt.isEmpty() || ticketHistoryOpt.isEmpty()) {
//            return FinalResponseDto.builder()
//                    .message("회원정보가 없거나 티켓이없음")
//                    .statusCode("3000")
//                    .build();
//        }
//
////        MemberEntity member = memberOpt.get();
//        TicketHistoryEntity ticketHistory = ticketHistoryOpt.get();
//        OffsetDateTime now = OffsetDateTime.now();
//        OffsetDateTime expiredTime=null;
//        if (ticketHistory.getEndDate() != null) {//티켓이 기간권이면
//            expiredTime = ticketHistory.getEndDate();
//        } else if (ticketHistory.getRemainTime() != null) {//티켓이 시간권이면
//            Duration remainTime = ticketHistory.getRemainTime();
//            expiredTime = now.plus(remainTime);
//        }
//        EnterHistoryEntity enterHistory = new EnterHistoryEntity(memberId, seat, ticketHistory, now, expiredTime);
//        enterHistoryRepository.save(enterHistory);
//        return FinalResponseDto.builder()
//                .message("입장완료 되었습니다.")
//                .statusCode("0000")
//                .build();
//    }
//
//    @Override
//    public FinalResponseDto out(Long userId){
//        //object가 any같은거면 쓰기가싫은데 <object>를안쓰는 방법은없나?
//        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(userId); //현재 어디앉았는지
//        if(enterHistory != null) {
//            OffsetDateTime now = OffsetDateTime.now();
//            enterHistory.setCloseTime(now);
//            enterHistoryRepository.save(enterHistory);
//
//            // TODO: 현재 티켓이 시간권인 경우. -> remainTime을 업데이트 한다.
//            // 자리점유 시 시간권인 경우 expiredTime 세팅!
//            // redis에서 expiredTime 관련 이벤트 생성. expired 됐을 때.. 자동 퇴장 시키는 로직도 만들어야 함.
//                // ㄴ 티켓 만료 시켜야 함.
//
//            return FinalResponseDto.builder()
//                    .message("퇴장이 완료 되었습니다.")
//                    .statusCode("0000")
//                    .build();
//        }
//        return FinalResponseDto.builder()
//                .message("자리정보가 없습니다")
//                .statusCode("1006")
//                .build();
//
//
//    }
//
//    @Override
//    public FinalResponseDto move(Long userId, Long movingRoomCode, int movingSeatNumber){
//        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(userId);
//        if (enterHistory == null) {
//            return FinalResponseDto.builder()
//                    .message("입장정보가 없습니다")
//                    .statusCode("1006")
//                    .build();
//        }
//        SeatEntity currentSeat = enterHistory.getSeat();
//        Optional<SeatEntity> seatOpt = seatRepository.findBySeatCodeAndRoom_Id(currentSeat.getSeatCode(), currentSeat.getRoom().getId()); //자리체크
//        if (seatOpt.isEmpty()) {
//            return FinalResponseDto.builder()
//                    .message("자리정보가 없습니다")
//                    .statusCode("1006")
//                    .build();
//        }
//        Optional<SeatEntity> newSeatOpt = seatRepository.findBySeatCodeAndRoom_Id(movingSeatNumber, movingRoomCode);
//        if(newSeatOpt.isPresent()){
//            SeatEntity newSeat = newSeatOpt.get();
//            if (!newSeat.getAvailable()) {//자리가 이미 차있으면
//                return FinalResponseDto.builder()
//                        .message("자리가 이미차있습니다")
//                        .statusCode("1006")
//                        .build();
//            }
//            // TODO: 기존 Seat에 대해서도 업데이트 필요
//            newSeat.setAvailable(false);
//            seatRepository.save(newSeat);
//            enterHistory.setSeat(newSeat);
//            enterHistoryRepository.save(enterHistory);
//        }
//
//        return FinalResponseDto.builder()
//                .message("자리 이동이 성공적으로 완료되었습니다")
//                .statusCode("0000")
//                .build();
//    }
//
//
//    ///
//    public void sendCodeToEmail(String toEmail) {//이 함수는 뭘뜻하는지 잘모르겠다..
//        this.checkDuplicatedEmail(toEmail);
//        String title = " 이메일 인증 번호";
//        String authCode = this.createCode();
//        MailService.sendEmail(toEmail, title, authCode);
//        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
//        RedisServiceImpl.setValues(AUTH_CODE_PREFIX + toEmail,
//                authCode, Duration.ofMillis(this.authCodeExpirationMillis));
//    }
//
//    private FinalResponseDto checkDuplicatedEmail(String email) {
//        boolean isMemberExist = shopRepository.existsByEmail(email);
//        if (isMemberExist) {
//            return FinalResponseDto.builder()
//                    .message("해당 점주가 이미 존재합니다.")
//                    .statusCode("0000")
//                    .build();
//        }
//    }
//
//    private String createCode() {
//        int lenth = 6;
//        try {
//            Random random = SecureRandom.getInstanceStrong();
//            StringBuilder builder = new StringBuilder();
//            for (int i = 0; i < lenth; i++) {
//                builder.append(random.nextInt(10));
//            }
//            return builder.toString();
//        } catch (NoSuchAlgorithmException e) {
//            log.debug("MemberService.createCode() exception occur");
//            throw new BusinessLogicException(ExceptionCode.NO_SUCH_ALGORITHM);
//        }
//    }
//
//    public EmailVerificationResult verifiedCode(String email, String authCode) {
//        this.checkDuplicatedEmail(email);
//        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
//        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);
//
//        return EmailVerificationResult.of(authResult);
//    }
//
//}
