package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;

import com.example.studyroom.security.JwtCookieUtil;
import com.example.studyroom.security.JwtUtil;
import com.example.studyroom.type.ApiResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.concurrent.CompletableFuture;

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
    private final DeleteMemberRepository deleteMemberRepository;

    private final PeriodTicketServiceImpl periodTicketServiceImpl;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository repository,
                            EnterHistoryRepository enterHistoryRepository,
                             SeatRepository seatRepository,

                             ShopRepository shopRepository,
                             RoomRepository roomRepository, MemberRepository memberRepository,
                             MailService mailService, RedisService redisService,
                             RemainPeriodTicketRepository remainPeriodTicketRepository,
                             DeleteMemberRepository deleteMemberRepository,
                             RemainTimeTicketRepository remainTimeTicketRepository, JwtUtil jwtUtil,
                             PeriodTicketServiceImpl periodTicketServiceImpl
                            , PasswordEncoder passwordEncoder) {
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
        this.deleteMemberRepository = deleteMemberRepository;
        this.periodTicketServiceImpl = periodTicketServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public List<MemberEntity> findByShop(ShopEntity shop) {
        return repository.findByShop(shop);
    }


//    @Override //로그인
//    public FinalResponseDto<String> login(MemberSignInRequestDto dto, HttpServletResponse response) {
//        //레포지토리에있는 함수가져오기
//        System.out.println("로그인때 비밀번호"+passwordEncoder.encode(dto.getPassword()));
//        MemberEntity Member = repository.findByPhoneAndPassword(dto.getPhoneNumber(), passwordEncoder.encode(dto.getPassword()));
//
//        if (Member != null) {
//            String token = this.jwtUtil.createAccessToken(dto);
//            JwtCookieUtil.addInfoToCookie(String.valueOf(dto.getShopId()), response, 3600);
//            return FinalResponseDto.successWithData(token);
//            //return Member;
//        } else {
//            return FinalResponseDto.failure(ApiResult.AUTHENTICATION_FAILED);
//            //throw new RuntimeException("로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.");
//        }
//    }

    @Override // 로그인
    public FinalResponseDto<String> login(MemberSignInRequestDto dto, HttpServletResponse response) {
        // 레포지토리에서 사용자 조회
        MemberEntity existingMember = repository.findByPhone(dto.getPhoneNumber());

        if (existingMember == null) {
            return FinalResponseDto.failure(ApiResult.AUTHENTICATION_FAILED); // 사용자 없음 처리
        }

        // 사용자가 존재하는지 확인
        if (passwordEncoder.matches(dto.getPassword(), existingMember.getPassword())) {
            String token = this.jwtUtil.createAccessToken(existingMember);
            JwtCookieUtil.addInfoToCookie(String.valueOf(dto.getShopId()), response, 3600);
            return FinalResponseDto.successWithData(token);
        } else {
            return FinalResponseDto.failure(ApiResult.AUTHENTICATION_FAILED);
        }
    }


    @Override //로그아웃
    public FinalResponseDto<String> outAndlogout(MemberEntity member ,String accessToken) {

        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(member.getId());
        if(enterHistory != null && enterHistory.getExitTime()==null) { //따로 자리퇴장요청을 하지않고 바로 로그아웃했을땐 자리를 빼야하니까

            FinalResponseDto<String> outResponse = out(member.getId()); // out 메서드를 호출하여 좌석 퇴장 처리

            if (outResponse.getMessage().equals(ApiResult.DATA_NOT_FOUND.name())) {

                return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
            }
        }
        //redis에 남아있던 리프레시토큰 제거
        String refreshTokenKey = "refreshToken:" + member.getId();  // 해당 사용자에 해당하는 refreshToken Redis 키
        FinalResponseDto<String> deleteResponse=  redisService.deleteValue(refreshTokenKey);
        if(deleteResponse.getMessage().equals(ApiResult.FAIL.name())){
            return FinalResponseDto.failure(ApiResult.FAIL);
        }
        //액세스토큰의 남은 유효기간만큼 redis에서 블랙리스트에 저장
        String accessTokenKey = "blacklist:accessToken:" + member.getId();
        jwtUtil.setAccessTokenWithRemainingTTL(accessTokenKey, accessToken);

        return FinalResponseDto.success(); // 로그아웃 성공

    }



    @Override
    public FinalResponseDto<MemberEntity> signUp(MemberSignUpRequestDto member) {
        if (repository.existsByPhone(member.getPhone())) {
            return FinalResponseDto.failure(ApiResult.ALREADY_EXIST_PHONE);
        }
        Optional<ShopEntity> shopOptional = shopRepository.findById(member.getShopId());
        if(shopOptional.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.SHOP_NOT_FOUND);
        }
        System.out.println("회원가입때 비밀번호"+passwordEncoder.encode(member.getPassword()));
        MemberEntity createdMember = MemberEntity.builder()
                .phone(member.getPhone())
                .name(member.getName())
                .password(passwordEncoder.encode(member.getPassword()))
                .shop(shopOptional.get())
                .build();
        //createdMember.setShop(shopOptional.get());
        repository.save(createdMember);
        return FinalResponseDto.successWithData(createdMember);
    }

    @Override
    public FinalResponseDto<String> out(Long userId) {
        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(userId);
        if (enterHistory == null) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        updateExitTime(enterHistory);
        boolean isUpdateSeatAvailability = updateSeatAvailability(enterHistory.getSeat());

        if(!isUpdateSeatAvailability){
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        boolean ticketHandled = handleTicketExit(userId);
        if (!ticketHandled) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        return FinalResponseDto.success();
    }

    @Override
    public void updateExitTime(EnterHistoryEntity enterHistory) {
        enterHistory.setExitTime(OffsetDateTime.now());
        enterHistoryRepository.save(enterHistory);
    }
    @Override
    public boolean updateSeatAvailability(SeatEntity seat) {
        Optional<SeatEntity> seatOpt = seatRepository.findBySeatCodeAndRoom_Id(seat.getSeatCode(), seat.getRoom().getId());
        if (seatOpt.isPresent()) {
            SeatEntity updatedSeat = seatOpt.get();
            updatedSeat.setAvailable(true);
            seatRepository.save(updatedSeat);
            return true;
        } else {
            return false;
        }
    }

    private boolean handleTicketExit(Long userId) {
        if (handlePeriodTicket(userId)) { //기간권먼저체크
            return true;
        }
        return handleTimeTicket(userId); //그담에 시간권체크
    }

    private boolean handlePeriodTicket(Long userId) {
        String periodKeyPattern = "periodSeat:*:user:" + userId + ":*";
        String periodRedisKey = redisService.findMatchingKey(periodKeyPattern);
        if (periodRedisKey != null) {
            redisService.deleteValue(periodRedisKey);
            return true;
        }
        return false;
    }

    private boolean handleTimeTicket(Long userId) {
        String timeKeyPattern = "timeSeat:*:user:" + userId + ":*";
        String timeRedisKey = redisService.findMatchingKey(timeKeyPattern);
        if (timeRedisKey != null) {
            long remainingSeconds = redisService.getTTL(timeRedisKey);
            if (remainingSeconds > 0) {
               boolean isUpdateRemainTimeTicket = updateRemainTimeTicket(userId, remainingSeconds);
               if (!isUpdateRemainTimeTicket) {
                   return false;
               }
            }
            redisService.deleteValue(timeRedisKey);
            return true;
        }
        return false;
    }

    private boolean updateRemainTimeTicket(Long userId, long remainingSeconds) {
        Optional<RemainTimeTicketEntity> remainTimeTicketOpt = remainTimeTicketRepository.findByMemberId(userId);
        if (remainTimeTicketOpt.isEmpty()) {
            return false;
        }
        RemainTimeTicketEntity remainTimeTicket = remainTimeTicketOpt.get();
        Duration remainingDuration = Duration.ofSeconds(remainingSeconds);
        remainTimeTicket.setRemainTime(remainingDuration);
        remainTimeTicketRepository.save(remainTimeTicket);
        return true;
    }

    @Override
    // TODO: Get Seat ID... 현재 유저가 자리하는 곳 반환
    public FinalResponseDto<MySeatInfoResponseDto> getSeatId(Long userId) {
        EnterHistoryEntity enterHistory= enterHistoryRepository.findActiveByCustomerId(userId);
        if(enterHistory==null){
            return FinalResponseDto.failure(ApiResult.SEAT_NOT_FOUND);
        }
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
    public FinalResponseDto<String> occupySeatAndHandleTicket( MemberEntity member,OccupySeatRequestDto requestDto) {
        Optional<ShopEntity> shopOpt = (shopRepository.findById(member.getShop().getId()));
        if(shopOpt.isEmpty()) return FinalResponseDto.failure(ApiResult.SHOP_NOT_FOUND);

        Optional<RoomEntity> roomOpt = roomRepository.findById(requestDto.getRoomId());//룸체크
        if(roomOpt.isEmpty()) return FinalResponseDto.failure(ApiResult.ROOM_NOT_FOUND);

        Long roomId = roomOpt.get().getId();
        Optional<SeatEntity> seatOpt = seatRepository.findBySeatCodeAndRoom_Id(requestDto.getSeatCode(), roomId);//자리체크
        if(seatOpt.isEmpty()) return FinalResponseDto.failure(ApiResult.SEAT_NOT_FOUND);

        SeatEntity seat = seatOpt.get();
        if(!seat.getAvailable()) return FinalResponseDto.failure(ApiResult.SEAT_ALREADY_OCCUPIED);

        Optional<MemberEntity> memberOpt = repository.findById(member.getId());
        if(memberOpt.isEmpty()) return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        return handleTicketOccupation(member, seat);

    }


    private FinalResponseDto<String> handleTicketOccupation(MemberEntity member, SeatEntity seat) {
        Optional<RemainPeriodTicketEntity> optionalRemainPeriodTicket = remainPeriodTicketRepository.findByShopIdAndMemberId(member.getShop().getId(), member.getId());
        Optional<RemainTimeTicketEntity> optionalRemainTimeTicket = remainTimeTicketRepository.findByShopIdAndMemberId(member.getShop().getId(), member.getId());

        if (optionalRemainPeriodTicket.isPresent()) {
            return handlePeriodTicket(member, seat, optionalRemainPeriodTicket.get());
        } else if (optionalRemainTimeTicket.isPresent()) {
            return handleTimeTicket(member, seat, optionalRemainTimeTicket.get());
        } else {
            return FinalResponseDto.failure(ApiResult.TICKET_NOT_FOUND);
        }
    }

    private FinalResponseDto<String> handlePeriodTicket(MemberEntity member, SeatEntity seat, RemainPeriodTicketEntity remainPeriodTicket) {
        OffsetDateTime endDate = remainPeriodTicket.getEndDate();
        OffsetDateTime now = OffsetDateTime.now();
        long ttlSeconds = Duration.between(now, endDate).getSeconds();

        if (ttlSeconds > 0) {
            String redisKey = "periodSeat:" + seat.getId() + ":user:" + member.getId() + ":shop:" + member.getShop().getId();
            redisService.setValuesWithTTL(redisKey, "occupied", ttlSeconds);

            seat.setAvailable(false);
            seatRepository.save(seat);

            EnterHistoryEntity enterHistory = EnterHistoryEntity.builder().member(member).seat(seat).enterTime(now).shop(member.getShop()).build();
            enterHistoryRepository.save(enterHistory);
            return FinalResponseDto.success();
        } else {
            return FinalResponseDto.failure(ApiResult.EXPIRED_TICKET);
        }
    }

    private FinalResponseDto<String> handleTimeTicket(MemberEntity member, SeatEntity seat, RemainTimeTicketEntity remainTimeTicket) {
        Duration remainTime = remainTimeTicket.getRemainTime();
        long millis = remainTime.toMillis();

        seat.setAvailable(false);
        seatRepository.save(seat);

        String redisKey = "timeSeat:" + seat.getId() + ":user:" + member.getId() + ":shop:" + member.getShop().getId();
        redisService.setValues(redisKey, "occupied", Duration.ofMillis(millis));

        OffsetDateTime now = OffsetDateTime.now();
        EnterHistoryEntity enterHistory = EnterHistoryEntity.builder().member(member).seat(seat).enterTime(now).shop(member.getShop()).build();
        enterHistoryRepository.save(enterHistory);
        return FinalResponseDto.success();
    }


    @Override
    public FinalResponseDto<RemainTicketInfoResponseDto> getRemainTime(Long shopId, Long userId) {

        String matchingKey = redisService.getUsingTicketCategoryInfoByUserId(userId);
        if(matchingKey==null){
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        String ticketCategory = matchingKey.substring(0, matchingKey.indexOf(':'));
        if(ticketCategory.equals("timeSeat")){//현재 시간권 사용하면
            RemainTicketInfoResponseDto result = redisService.getReaminTimeInfoByUserId(matchingKey,ticketCategory);
            return FinalResponseDto.successWithData(result);
        }else if(ticketCategory.equals("periodSeat")){
            RemainTicketInfoResponseDto result =  periodTicketServiceImpl.getEndDate(shopId,userId,ticketCategory);
            return FinalResponseDto.successWithData(result);
        }
        return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);

    }

    @Override
    public FinalResponseDto<String> moveAndHandleTicket(MemberEntity member, MemberMoveRequestDto requestDto) {
        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(member.getId());
        if (enterHistory == null) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        // 현재 자리와 새 자리 유효성 검사
        Optional<SeatEntity> currentSeatOpt = seatRepository.findBySeatCodeAndRoom_Id(
                enterHistory.getSeat().getSeatCode(), enterHistory.getSeat().getRoom().getId()
        );
        Optional<SeatEntity> newSeatOpt = seatRepository.findBySeatCodeAndRoom_Id(
                requestDto.getSeatCode(), requestDto.getRoomId()
        );

        FinalResponseDto<String> validationResponse = validateSeats(currentSeatOpt, newSeatOpt);
        if (!validationResponse.getMessage().matches(FinalResponseDto.success().getMessage())) {
            return validationResponse;
        }
        if(currentSeatOpt.isEmpty() || newSeatOpt.isEmpty()){
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        SeatEntity currentSeat = currentSeatOpt.get();
        SeatEntity newSeat = newSeatOpt.get();
        // 기존 자리 비우기 및 상태 변경 처리
        FinalResponseDto<String> outResult = handleSeatExit(member.getId(), currentSeat);
        if (!validationResponse.getMessage().matches(FinalResponseDto.success().getMessage())) {
            return outResult;
        }

        // 새 자리 점유 처리
        return handleTicketOccupation(member, newSeat);
    }

    // 현재 자리와 새 자리 유효성 검사
    private FinalResponseDto<String> validateSeats(Optional<SeatEntity> currentSeatOpt, Optional<SeatEntity> newSeatOpt) {
        if (currentSeatOpt.isEmpty() || newSeatOpt.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.SEAT_NOT_FOUND);
        }

        SeatEntity newSeat = newSeatOpt.get();
        if (!newSeat.getAvailable()) {
            return FinalResponseDto.failure(ApiResult.SEAT_ALREADY_OCCUPIED);
        }

        return FinalResponseDto.success();
    }

    // 기존 자리 비우기 처리
    private FinalResponseDto<String> handleSeatExit(Long memberId, SeatEntity currentSeat) {
        FinalResponseDto<String> outResult = out(memberId);
        if (!outResult.getMessage().matches(FinalResponseDto.success().getMessage())) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        // 자리 상태 변경
        currentSeat.setAvailable(true);
        seatRepository.save(currentSeat);

        return FinalResponseDto.success();
    }





    @Override
    public FinalResponseDto<String> resetPwAndLogout(MemberEntity tokenMember, ResetPwRequestDto requestDto , String accessToken) {
//        MemberEntity member = memberRepository.findById(tokenMember.getId()).orElseThrow(() -> new RuntimeException(ApiResult.DATA_NOT_FOUND.getCode()));

        MemberEntity member = memberRepository.findById(tokenMember.getId()).orElse(null);
        if(member == null) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        System.out.println("재설정 비밀번호"+passwordEncoder.encode(requestDto.getNewPassword()));
        System.out.println("기존 비밀번호"+passwordEncoder.encode(requestDto.getPassword()));
        System.out.println(passwordEncoder.matches(requestDto.getPassword(), member.getPassword()));
        if(!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            return FinalResponseDto.failure(ApiResult.AUTHENTICATION_FAILED);
        }
        member.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        this.outAndlogout(member, accessToken);
        memberRepository.save(member);
        return FinalResponseDto.success();
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

    @Override
    @Transactional
    public FinalResponseDto<String> deleteMemberAndLogout(MemberEntity memberEntity ,String accessToken){
        Optional<MemberEntity> member = memberRepository.findById(memberEntity.getId());
        if(member.isEmpty()){
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        this.outAndlogout(memberEntity, accessToken);
        // 탈퇴 전 회원 정보를 DeletedMemberEntity에 저장
        DeletedMemberEntity deletedMember = DeletedMemberEntity.builder()
                .shop(member.get().getShop()) // 탈퇴한 회원이 속한 상점 정보
                .name(member.get().getName()) // 회원 이름
                .phone(member.get().getPhone()) // 회원 전화번호
                .password(member.get().getPassword()) // 회원 패스워드
                .deleteTime(OffsetDateTime.now()) // 탈퇴 시간
                .build();

        // DeletedMemberEntity에 저장
        deleteMemberRepository.save(deletedMember);

        memberRepository.deleteById(member.get().getId());
        return FinalResponseDto.success();
    }

}


