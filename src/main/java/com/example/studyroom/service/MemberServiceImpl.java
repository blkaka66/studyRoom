package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.MemberSignInRequestDto;
import com.example.studyroom.dto.requestDto.MemberSignUpRequestDto;
import com.example.studyroom.dto.requestDto.OccupySeatRequestDto;
import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.MemberResponseDto;
import com.example.studyroom.dto.responseDto.MySeatInfoResponseDto;
import com.example.studyroom.dto.responseDto.RemainTimeResponseDto;
import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;

import com.example.studyroom.security.JwtCookieUtil;
import com.example.studyroom.security.JwtUtil;
import com.example.studyroom.type.ApiResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class MemberServiceImpl extends BaseServiceImpl<MemberEntity> implements MemberService {

    private final MemberRepository repository;
    private final EnterHistoryRepository enterHistoryRepository;
    private final JwtUtil jwtUtil;

    private final SeatRepository seatRepository;

    private final ShopRepository shopRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;

    private final MailService mailService;
    private final RedisService redisService;

    private static final String AUTH_CODE_PREFIX = "EMAIL_VERIFICATION_KEY:";
    private static final long authCodeExpirationMillis = 60*60*1000;
    private final RemainPeriodTicketRepository remainPeriodTicketRepository;
    private final RemainTimeTicketRepository remainTimeTicketRepository;

    public MemberServiceImpl(MemberRepository repository,
                            EnterHistoryRepository enterHistoryRepository,
                             SeatRepository seatRepository,

                             ShopRepository shopRepository,
                             RoomRepository roomRepository, MemberRepository memberRepository,
                             MailService mailService, RedisService redisService,
                             RemainPeriodTicketRepository remainPeriodTicketRepository,
                             RemainTimeTicketRepository remainTimeTicketRepository, JwtUtil jwtUtil) {
        super(repository);
        this.repository = repository;
        this.enterHistoryRepository = enterHistoryRepository;
        this.jwtUtil = jwtUtil;
        this.seatRepository = seatRepository;
        this.shopRepository = shopRepository;

        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;

        this.mailService = mailService;
        this.redisService = redisService;
        //this.remainTicketRepository = remainTicketRepository;
        this.remainPeriodTicketRepository = remainPeriodTicketRepository;
        this.remainTimeTicketRepository = remainTimeTicketRepository;
    }


    @Override
    public List<MemberEntity> findByShop(ShopEntity shop) {
        return repository.findByShop(shop);
    }


    //자리선택시 enterhistory enterTime까지 생성하는 메서드 만들어야함
    @Override //로그인
    public FinalResponseDto<String> login(MemberSignInRequestDto dto, HttpServletResponse response) {
        //레포지토리에있는 함수가져오기
        MemberEntity Member = repository.findByPhoneAndPassword(dto.getPhoneNumber(), dto.getPassword());

        if (Member != null) {
            String token = this.jwtUtil.createAccessToken(dto);
            JwtCookieUtil.addInfoToCookie(String.valueOf(dto.getShopId()), response, 3600);
            return FinalResponseDto.successWithData(token);
            //return Member;
        } else {
            return FinalResponseDto.failure(ApiResult.AUTHENTICATION_FAILED);
            //throw new RuntimeException("로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    @Override //회원가입 // TODO - 암호화 필요
    public FinalResponseDto<MemberEntity> signUp(MemberSignUpRequestDto member) {

        if (repository.existsByPhone(member.getPhone())) {
            return FinalResponseDto.failure(ApiResult.ALREADY_EXIST_PHONE);
        }
        Optional<ShopEntity> shopOptional = shopRepository.findById(member.getShopId());
        if(!shopOptional.isPresent()) {
            return FinalResponseDto.failure(ApiResult.SHOP_NOT_FOUND);
        }

        MemberEntity createdMember = member.toEntity();
        createdMember.setShop(shopOptional.get());
        repository.save(createdMember);
        return FinalResponseDto.successWithData(createdMember);
        // return repository.save(shop);
    }


    @Override
    public FinalResponseDto<RemainTimeResponseDto> getRemainTime(Long shopId, Long userId) {
        // 만료되지 않은 티켓 히스토리 엔티티 정보 가져오기

        String ticketExpireTime = "";
        LocalDate endDate = null;

        Optional<RemainPeriodTicketEntity> remainPeriodTicket = remainPeriodTicketRepository.findByShopIdAndMemberId(shopId, userId);
        if(remainPeriodTicket.isPresent()){ //기간권
            OffsetDateTime TimeTicketEndDate = remainPeriodTicket.get().getEndDate();
            endDate = TimeTicketEndDate.toLocalDate();

        }
        Optional<RemainTimeTicketEntity> remainTimeTicket = remainTimeTicketRepository.findByShopIdAndMemberId(shopId, userId);
        if(remainTimeTicket.isPresent()){//시간권
            Duration totalRemainTime = Duration.ZERO;
            Duration remainTime = remainTimeTicket.get().getRemainTime();
            if (totalRemainTime.toHours() > 0) {
                ticketExpireTime = totalRemainTime.toHours() + "시간";
            }
        }

        if(remainPeriodTicket.isEmpty() && remainTimeTicket.isEmpty()){
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        // DTO로 변환하여 반환
        RemainTimeResponseDto remainTimeResponseDto= RemainTimeResponseDto.builder()
                .periodRemainTime(ticketExpireTime)
                .timeTicketEndDate(endDate)
                .build();

        return FinalResponseDto.successWithData(remainTimeResponseDto);




    }

    @Override
    // TODO: Get Seat ID... 현재 유저가 자리하는 곳 반환
    public FinalResponseDto<MySeatInfoResponseDto> getSeatId(Long userId) {
        EnterHistoryEntity enterHistory= enterHistoryRepository.findActiveByCustomerId(userId);
        Long id = enterHistory.getSeatId();
        MySeatInfoResponseDto responseDto = MySeatInfoResponseDto.builder()
                .seatId(id)
                .build();
        return FinalResponseDto.successWithData(responseDto);
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



    @Override
    @Transactional
    public FinalResponseDto<String> occupySeat( MemberEntity member,OccupySeatRequestDto requestDto) {
        System.out.println("안녕하세");
        Optional<ShopEntity> shopOpt = (shopRepository.findById(member.getShop().getId()));
        if(shopOpt.isEmpty()) return FinalResponseDto.failure(ApiResult.SHOP_NOT_FOUND);

        Optional<RoomEntity> roomOpt = roomRepository.findById(requestDto.getRoomId());//룸체크
        if(roomOpt.isEmpty()) return FinalResponseDto.failure(ApiResult.ROOM_NOT_FOUND);

        Long roomId = roomOpt.get().getId();
        Optional<SeatEntity> seatOpt = seatRepository.findBySeatCodeAndRoom_Id(requestDto.getSeatCode(), roomId);//자리체크
        if(seatOpt.isEmpty()) return FinalResponseDto.failure(ApiResult.SEAT_NOT_FOUND);

        SeatEntity seat = seatOpt.get();
        if(!seat.getAvailable()) return FinalResponseDto.failure(ApiResult.SEAT_ALREADY_OCCUPIED);


        seat.setAvailable(false);
        seatRepository.save(seat);//윗줄 false저장

        Optional<MemberEntity> memberOpt = repository.findById(member.getId());
        if(memberOpt.isEmpty()) return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);


        System.out.println("안녕하세");

        Optional<RemainPeriodTicketEntity> optionalRemainPeriodTicket = remainPeriodTicketRepository.findByShopIdAndMemberId(member.getShop().getId(), member.getId());
        Optional<RemainTimeTicketEntity> optionalRemainTimeTicket = remainTimeTicketRepository.findByShopIdAndMemberId(member.getShop().getId(), member.getId());

        if(optionalRemainPeriodTicket.isPresent()){ //기간권있으면 기간권먼저
            RemainPeriodTicketEntity remainPeriodTicket = optionalRemainPeriodTicket.get();

            OffsetDateTime endDate = remainPeriodTicket.getEndDate();
            OffsetDateTime now = OffsetDateTime.now();
            long ttlSeconds = Duration.between(now, endDate).getSeconds();

            if (ttlSeconds > 0) {
                String redisKey = "periodSeat:" +seat.getId()+ ":user:" + member.getId() + ":shop:" + member.getShop().getId();
                redisService.setValuesWithTTL(redisKey, "occupied", ttlSeconds);

            } else {
                return FinalResponseDto.failure(ApiResult.EXPIRED_TICKET);
            }



            EnterHistoryEntity enterHistory = EnterHistoryEntity.builder().member(member).seat(seat).enterTime(now).build();
            enterHistoryRepository.save(enterHistory);
            return FinalResponseDto.success();

        }else if(optionalRemainTimeTicket.isPresent()){ //시간권 밖에없으면 시간권사용
            RemainTimeTicketEntity remainTimeTicket = optionalRemainTimeTicket.get();
            Duration remainTime = remainTimeTicket.getRemainTime();
            long millis = remainTime.toMillis();//남은시간을 밀리초로 변환


            String redisKey = "timeSeat:" +seat.getId()+ ":user:" + member.getId()  + ":shop:" +member.getShop().getId();
            redisService.setValues(redisKey, "occupied", Duration.ofMillis(millis));

            OffsetDateTime now = OffsetDateTime.now();
            EnterHistoryEntity enterHistory = EnterHistoryEntity.builder().member(member).seat(seat).enterTime(now).build();

            enterHistoryRepository.save(enterHistory);
            return FinalResponseDto.success();
        }

        else{//티켓남은시간없는경우
            return FinalResponseDto.failure(ApiResult.TICKET_NOT_FOUND);
        }



    }

    @Override
    public FinalResponseDto<String> out(Long userId){

        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(userId); //현재 어디앉았는지
        if(enterHistory != null) {
            OffsetDateTime now = OffsetDateTime.now();
            enterHistory.setExitTime(now);
            enterHistoryRepository.save(enterHistory);


            return FinalResponseDto.success();

        }
        return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);



    }

    @Override
    public FinalResponseDto<String> move(Long userId, Long movingRoomCode, int movingSeatNumber){
        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(userId);
        if(enterHistory!=null){
            SeatEntity currentSeat = enterHistory.getSeat();
            Optional<SeatEntity> seatOpt = seatRepository.findBySeatCodeAndRoom_Id(currentSeat.getSeatCode(), currentSeat.getRoom().getId()); //자리체크
            Optional<SeatEntity> newSeatOpt = seatRepository.findBySeatCodeAndRoom_Id(movingSeatNumber, movingRoomCode);
            if(newSeatOpt.isPresent() && seatOpt.isPresent()){
                SeatEntity newSeat = newSeatOpt.get();
                SeatEntity previousSeat = seatOpt.get();
                if(newSeat.getAvailable()){
                    newSeat.setAvailable(false);
                    previousSeat.setAvailable(true);
                    seatRepository.save(newSeat);
                    seatRepository.save(previousSeat);
                    enterHistory.setSeat(newSeat);
                    enterHistoryRepository.save(enterHistory);
                    return FinalResponseDto.success();
                }else{
                    return FinalResponseDto.failure(ApiResult.SEAT_ALREADY_OCCUPIED);
                }
            }else{
                return FinalResponseDto.failure(ApiResult.SEAT_NOT_FOUND);
            }
        }else{
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }


    }

    ///
    //fixme:이메일기능에서 이 밑에함수들이 필요한가?
    public void sendCodeToEmail(String toEmail) {//이 함수는 뭘뜻하는지 잘모르겠다..
        this.checkDuplicatedEmail(toEmail); // 중복여부 체크

        String title = " 이메일 인증 번호";
        String authCode = this.createCode();
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        if(authCode != null){
            redisService.setValues(AUTH_CODE_PREFIX + toEmail, authCode, Duration.ofMillis(authCodeExpirationMillis));
            mailService.sendEmail(toEmail, title, authCode);
        }else{

        }

    }

    private FinalResponseDto<String> checkDuplicatedEmail(String email) {
        boolean isMemberExist = shopRepository.existsByEmail(email);
        if (isMemberExist) {
            return FinalResponseDto.failure(ApiResult.ALREADY_EXIST_EMAIL);
        }else{
            return FinalResponseDto.success();
        }
    }

    private String createCode() {
        int lenth = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public FinalResponseDto<Object> verifiedCode(String email, String authCode) {
        this.checkDuplicatedEmail(email);
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        if(redisAuthCode == null) {
            //return 유효시간 초과 에러 -->
            return FinalResponseDto.failure(ApiResult.TIMEOUT_EXCEEDED);
        }
        boolean authResult = authCode.equals(redisAuthCode);

        return FinalResponseDto.successWithData(authResult);
    }

    public FinalResponseDto<MemberResponseDto> getMemberInfo(Long userId){
        Optional<MemberEntity> member = memberRepository.findById(userId);
        if(member.isEmpty()){
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        MemberResponseDto responseDto = MemberResponseDto.builder()
                .name(member.get().getName())
                .phone(member.get().getPhone())
                .build();
        return FinalResponseDto.successWithData(responseDto);
    }

    public FinalResponseDto<String> deleteMember(Long userId){
        Optional<MemberEntity> member = memberRepository.findById(userId);
        if(member.isEmpty()){
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        memberRepository.deleteById(member.get().getId());
        return FinalResponseDto.success();
    }
}


