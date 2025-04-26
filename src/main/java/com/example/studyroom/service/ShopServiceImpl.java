package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.*;
import com.example.studyroom.model.statistics.*;
import com.example.studyroom.repository.*;
import com.example.studyroom.repository.statistics.*;
import com.example.studyroom.security.JwtCookieUtil;
import com.example.studyroom.security.JwtUtil;
import com.example.studyroom.type.ApiResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.studyroom.service.TicketServiceImpl.toOffsetDateTime;

@Slf4j
@Service
public class ShopServiceImpl extends BaseServiceImpl<ShopEntity> implements ShopService {
    private final ShopRepository repository;
    private final MemberService memberService;
    private final SeatRepository seatRepository;
    private final RoomRepository roomRepository;
    private final MemberServiceImpl memberServiceImpl;
    private final EnterHistoryRepository enterHistoryRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final JwtUtil jwtUtil;
    private final PeriodTicketRepository periodTicketRepository;
    private final TimeTicketRepository timeTicketRepository;
    private final RedisService redisService;
    private final AnnouncementRepository announcementRepository;
    private final CouponRepository couponRepository;
    private final ShopUsageDailyRepository shopUsageDailyRepository;
    private final SeatIdUsageRepository seatIdUsageRepository;
    private final ShopUsageHourlyRepository shopUsageHourlyRepository;
    private final UserAvrUsageRepository userAvrUsageRepository;
    private final TimeTicketHistoryRepository timeTicketHistoryRepository;
    private final PeriodTicketHistoryRepository periodTicketHistoryRepository;
    private final ShopDailyPaymentRepository shopDailyPaymentRepository;
    private final CustomerChangeStatsRepository customerChangeStatsRepository;
    private final TicketServiceImpl ticketServiceImpl;
    private final ChatSubscribeService chatSubscribeService;
    private final RedisTemplate<String, String> redisTemplate;

    public ShopServiceImpl(ShopRepository repository, MemberService memberService, SeatRepository seatRepository,
                           RoomRepository roomRepository, MemberServiceImpl memberServiceImpl
            , RedisService redisService,
                           EnterHistoryRepository enterHistoryRepository, MemberRepository memberRepository,
                           ShopRepository shopRepository, JwtUtil jwtUtil, PeriodTicketRepository periodTicketRepository,
                           TimeTicketRepository timeTicketRepository,
                           AnnouncementRepository announcementRepository,
                           CouponRepository couponRepository,
                           ShopUsageHourlyRepository shopUsageHourlyRepository,
                           ShopUsageDailyRepository shopUsageDailyRepository,
                           SeatIdUsageRepository seatIdUsageRepository,
                           UserAvrUsageRepository userAvrUsageRepository, TicketHistoryRepository ticketHistoryRepository,
                           TimeTicketHistoryRepository timeTicketHistoryRepository, PeriodTicketHistoryRepository periodTicketHistoryRepository
            , ShopDailyPaymentRepository shopDailyPaymentRepository, CustomerChangeStatsRepository customerChangeStatsRepository, TicketServiceImpl ticketServiceImpl
            , ChatSubscribeService chatSubscribeService, RedisTemplate<String, String> redisTemplate) {
        super(repository);
        this.repository = repository;
        this.memberService = memberService;
        this.seatRepository = seatRepository;
        this.roomRepository = roomRepository;
        this.memberServiceImpl = memberServiceImpl;
        this.enterHistoryRepository = enterHistoryRepository;
        this.memberRepository = memberRepository;
        this.shopRepository = shopRepository;
        this.redisService = redisService;
        this.jwtUtil = jwtUtil;
        this.periodTicketRepository = periodTicketRepository;
        this.timeTicketRepository = timeTicketRepository;
        this.announcementRepository = announcementRepository;
        this.couponRepository = couponRepository;
        this.shopUsageDailyRepository = shopUsageDailyRepository;
        this.seatIdUsageRepository = seatIdUsageRepository;
        this.shopUsageHourlyRepository = shopUsageHourlyRepository;
        this.userAvrUsageRepository = userAvrUsageRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.timeTicketHistoryRepository = timeTicketHistoryRepository;
        this.periodTicketHistoryRepository = periodTicketHistoryRepository;
        this.shopDailyPaymentRepository = shopDailyPaymentRepository;
        this.customerChangeStatsRepository = customerChangeStatsRepository;
        this.ticketServiceImpl = ticketServiceImpl;
        this.chatSubscribeService = chatSubscribeService;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public FinalResponseDto<List<MemberResponseDto>> getMemberListAndInfo(Long shopId) {
        Optional<ShopEntity> shop = this.findById(shopId);

        if (shop.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        } else {
            List<MemberEntity> members = shop.get().getMembers();

            List<MemberResponseDto> responseDtos = members.stream()
                    .map(member -> MemberResponseDto.builder()
                            .name(member.getName())
                            .phone(member.getPhone())
                            .userId(member.getId())
                            .createdAt(member.getCreatedAt())
                            .lastEnterTime(member.getLastEnterTime())
                            .build()
                    )
                    .collect(Collectors.toList());

            return FinalResponseDto.successWithData(responseDtos);
        }
    }


    @Override //지점목록 가져오기
    public FinalResponseDto<List<ShopEntity>> getShopList() {//shopId가 안들어오면 모든 리스트를 보내고 shopid가 들어오면 해당 shop리스트만 보내고
        //TODO:이 제네릭이 맞는지 모르겠다 리턴할 데이터의 자료형을쓰는게 맞나?
        return FinalResponseDto.successWithData(this.findAll());


    }


    @Override //로그인
    public FinalResponseDto<String> login(ShopSignInRequestDto dto, HttpServletResponse response) {
        //레포지토리에있는 함수가져오기
        //
        ShopEntity shop = repository.findByEmailAndPassword(dto.getEmail(), dto.getPassword());

        // TODO: Email 기준으로 Shop을 가져온 후
        //      - Shop이 존재하지 않는다면, 회원 존재하지 않는다는 오류 Response
        //      - Shop이 존재한다면, 암호화된 Password 를 비교

        if (shop != null) {
            String token = this.jwtUtil.createAccessToken(dto);
            this.jwtUtil.createRefreshToken(shop);
            JwtCookieUtil.addInfoToCookie(String.valueOf(dto.getShopId()), response, 3600);

            return FinalResponseDto.successWithData(token);

        } else {
            return FinalResponseDto.failure(ApiResult.AUTHENTICATION_FAILED);

        }
    }


    @Override
    public FinalResponseDto<String> logout(ShopEntity shop, String accessToken) {

        // Refresh 토큰 제거
        String refreshTokenKey = "refreshToken:shop:" + shop.getId();
        FinalResponseDto<String> deleteResponse = redisService.deleteValue(refreshTokenKey);
        if (deleteResponse.getMessage().equals(ApiResult.FAIL.name())) {
            return FinalResponseDto.failure(ApiResult.FAIL);
        }

        // 채팅 구독 전체 해제
        String userType = "shop";
        Long shopId = shop.getId();
        chatSubscribeService.unsubscribeAll(userType, shopId);

        // FCM 토큰 제거
        log.info("FCM 삭제 대상 key: fcm:{}:{}", userType, shopId);
        Boolean deleted = redisTemplate.delete("fcm:" + userType + ":" + shopId);

        log.info("FCM 토큰 삭제됨 여부: {}", deleted);

        // Access 토큰 블랙리스트 등록
        String accessTokenKey = "blacklist:accessToken:shop:" + shop.getId();
        jwtUtil.setAccessTokenWithRemainingTTL(accessTokenKey, accessToken);

        return FinalResponseDto.success();
    }


    @Override
    public FinalResponseDto<String> forceDeleteUser(ForceDeleteUserRequestDto dto) {
        Optional<MemberEntity> member = memberRepository.findById(dto.getUserId());
        if (member.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        if (!Objects.equals(member.get().getShop().getId(), dto.getShopId())) {
            return FinalResponseDto.failure(ApiResult.AUTHENTICATION_FAILED);
        }
        memberRepository.deleteById(dto.getUserId());
        return FinalResponseDto.success();
    }


    @Override //회원가입 // TODO - 암호화 필요
    public FinalResponseDto<ShopEntity> signUp(ShopSignUpRequestDto dto) {
        // TODO: of, success 사용
        // TODO: ApiResult 정의 후 사용
        if (repository.existsByEmail(dto.getEmail())) {
            return FinalResponseDto.failure(ApiResult.ALREADY_EXIST_EMAIL);
        }
        ShopEntity shop = dto.toEntity();
        repository.save(shop);
        return FinalResponseDto.successWithData(shop);
        // return repository.save(shop);
    }

    @Override // 지점정보가져오기
    public FinalResponseDto<ShopInfoResponseDto> getShopInfo(Long shopId) {
        if (shopId == null) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
            //throw new RuntimeException("존재하지않는 id");
        }
        Optional<ShopEntity> shop = repository.findById(shopId);
        if (shop.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
//            return FinalResponseDto.<ShopInfoResponseDto>builder()
//                    .message("존재하지 않는 지점입니다.")
//                    .statusCode("3000")
//                    .build();
        }


        ShopInfoResponseDto shopInfo = ShopInfoResponseDto.builder()
                .location(shop.get().getLocation()) //TODO:이거에러 어떻게없애지 질문?
                .name(shop.get().getName())
                .build();

        return FinalResponseDto.successWithData(shopInfo);


    }


    @Override
    public FinalResponseDto<List<RoomAndSeatInfoResponseDto>> getRoomsAndSeatsByShopId(Long customerId) {
        // EnterHistoryService를 사용하여 현재 사용자의 좌석 ID를 조회
        Long mySeatId = memberServiceImpl.getSeatIdByCustomerId(customerId);
        System.out.println("mySeatId" + mySeatId);
        Optional<MemberEntity> member = memberRepository.findById(customerId);
        // Shop ID로 방 목록을 조회
        if (member.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        Long shopId = member.get().getShop().getId();
        System.out.println("shopId" + shopId);
        List<RoomEntity> rooms = roomRepository.findByShopId(shopId);

        // 방 목록을 DTO로 변환
        List<RoomAndSeatInfoResponseDto> roomAndSeatInfoDtos = rooms.stream().map(room -> {
            // 각 방에 대한 좌석 목록을 DTO로 변환
            List<SeatinfoResponseDto> seatDtos = room.getSeats().stream().map(seat -> {
                // 현재 사용자의 좌석 ID와 mySeatId가 같으면 true 아니면 false
                boolean isMySeat = seat.getId().equals(mySeatId);

                // SeatinfoResponseDto 객체 생성
                return SeatinfoResponseDto.builder()
                        .id(seat.getId())
                        .available(seat.getAvailable())
                        .mySeat(isMySeat)  // mySeat 필드 설정
                        .onService(seat.getOnService())
                        .seatCode(seat.getSeatCode())
                        .build();
            }).collect(Collectors.toList());

            // RoomAndSeatInfoResponseDto 객체 생성
            return RoomAndSeatInfoResponseDto.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .onService(room.getOnService())
                    .seats(seatDtos)
                    .build();
        }).collect(Collectors.toList());

        // FinalResponseDto 객체 생성 및 반환
        return FinalResponseDto.successWithData(roomAndSeatInfoDtos);

    }

    //누가 자리점유요청 메시지창만 띄워도 점유가 되게 하고 다른 자리를 점유하면 그 자리는 점유를 풀기(어떻게하지?)->일단 보류

    @Override
    public FinalResponseDto<ProductResponseDto> getProductList(Long shopId) {

        List<PeriodTicketEntity> periodTicketEntities = periodTicketRepository.findByShopId(shopId);
        List<TimeTicketEntity> timeTicketEntities = timeTicketRepository.findByShopId(shopId);
        if (!periodTicketEntities.isEmpty()) {
            System.out.println("첫 번째 PeriodTicketEntity: " + periodTicketEntities.get(0).getName());
        } else {
            System.out.println("PeriodTicketEntity 리스트가 비어 있습니다.");
        }

//        if (!timeTicketEntities.isEmpty()) {
//            System.out.println("첫 번째 TimeTicketEntity: " + timeTicketEntities.get(0).getDays());
//        } else {
//            System.out.println("TimeTicketEntity 리스트가 비어 있습니다.");
//        }
        ProductResponseDto productResponseDto = ProductResponseDto.builder()
                .periodTicketList(periodTicketEntities)
                .timeTicketList(timeTicketEntities)
                .build();
        System.out.println(productResponseDto);

        return FinalResponseDto.successWithData(productResponseDto);

    }


    @Override
    public FinalResponseDto<String> createAnnounement(Long shopId, CreateAnnouncementRequestDto dto) {

        Optional<ShopEntity> shopOptional = shopRepository.findById(shopId);
        if (shopOptional.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.SHOP_NOT_FOUND);
        }
        System.out.println("제목" + dto.getTitle());
        System.out.println("본문" + dto.getContent());
        ShopEntity shop = shopOptional.get();
        OffsetDateTime now = OffsetDateTime.now();
        AnnouncementEntity announcement = AnnouncementEntity.builder()
                .shop(shop)
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(now)
                .announcementType(dto.getAnnouncementType())
                .isActive(true)
                .build();

        announcementRepository.save(announcement);


        return FinalResponseDto.success();

    }


    @Override
    public FinalResponseDto<List<AnnouncementResponseDto>> getAnnouncementList(Long shopId) {
        Optional<ShopEntity> shopOptional = shopRepository.findById(shopId);
        if (shopOptional.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.SHOP_NOT_FOUND);
        }

        List<AnnouncementEntity> announcements = announcementRepository.findByShopIdAndIsActiveTrue(shopId);

        List<AnnouncementResponseDto> responseDtos = announcements.stream()
                .map(AnnouncementResponseDto::convertToDto)
                .collect(Collectors.toList());

        return FinalResponseDto.successWithData(responseDtos);
    }


    @Override
    public FinalResponseDto<AnnouncementResponseDto> getAnnouncementInfo(Long docsId) {
        Optional<AnnouncementEntity> announcement = announcementRepository.findByIdAndIsActiveTrue(docsId);

        if (announcement.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.SHOP_NOT_FOUND);
        }

        AnnouncementResponseDto responseDto = AnnouncementResponseDto.convertToDto(announcement.get());

        return FinalResponseDto.successWithData(responseDto);
    }


    @Override
    public FinalResponseDto<CouponInfoResponseDto> getCouponInfo(String couponCode, Long shopId) {
        Optional<CouponEntity> coupon = Optional.ofNullable(couponRepository.findByCouponCodeAndShopId(couponCode, shopId));

        if (coupon.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.COUPON_NOT_FOUND);
        }

        // CouponEntity를 CouponInfoResponseDto로 변환
        CouponInfoResponseDto responseDto = CouponInfoResponseDto.builder()
                .id(coupon.get().getId())
                .couponName(coupon.get().getCouponName())
                .discountType(coupon.get().getDiscountType())
                .discountAmount(coupon.get().getDiscountAmount())
                .build();

        // FinalResponseDto에 담아서 반환
        return FinalResponseDto.successWithData(responseDto);
    }


    @Override
    public FinalResponseDto<List<SeatUsageStatsResponseDto>> getSeatUsageStats(Long shopId) {
        // shopId로 좌석 이용 통계 데이터 조회
        List<EnterHistoryEntity> enterHistory = enterHistoryRepository.findByShop_Id(shopId);

        if (enterHistory.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        // EnterHistoryEntity 목록을 SeatUsageStatsResponseDto 목록으로 변환
        List<SeatUsageStatsResponseDto> responseDtos = enterHistory.stream()
                .map(history -> SeatUsageStatsResponseDto.builder()
                        .id(history.getId())
                        .enterTime(history.getEnterTime())
                        .exitTime(history.getExitTime())
                        .shopId(history.getShop().getId())  // shopId로 변환
                        .seatId(history.getSeatId())
                        .memberId(history.getMember() != null ? history.getMember().getId() : null)  // memberId 처리
                        .build())
                .collect(Collectors.toList());

        // 변환된 데이터 반환
        return FinalResponseDto.successWithData(responseDtos);
    }

    @Override
    @Transactional
    public void calculateAndSaveshopUsageHourly() {
        OffsetDateTime now = OffsetDateTime.now();
        List<ShopEntity> shops = shopRepository.findAll();

        for (ShopEntity shop : shops) {
            int occupancyCount = enterHistoryRepository.countActiveEntriesByShopId(shop.getId(), now);
            ShopUsageHourlyEntity occupancy = ShopUsageHourlyEntity.from(shop, now, occupancyCount);
            shopUsageHourlyRepository.save(occupancy);  // 수정된 부분
        }
    }

    @Override
    @Transactional
    public void calculateAndSaveDailyOccupancy() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime yesterday = now.minusDays(1);  // 어제 날짜
        OffsetDateTime startOfDay = yesterday.toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();  // 어제 00:00:00
        OffsetDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);  // 어제 23:59:59.999999

        List<ShopEntity> shops = shopRepository.findAll();
        for (ShopEntity shop : shops) {
            // 해당 날짜에 해당 상점에서 이용한 사용자 수 카운트
            int uniqueUsersCount = enterHistoryRepository.countUniqueUsersByShopIdAndDate(shop.getId(), startOfDay, endOfDay);

            // 일일 통계 저장
            ShopUsageDailyEntity dailyOccupancy = ShopUsageDailyEntity.from(shop, yesterday, uniqueUsersCount);
            shopUsageDailyRepository.save(dailyOccupancy);
        }
    }


    @Override
    @Transactional
    public void calculateAndSaveUsageStatistics() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime yesterday = now.minusDays(1);  // 어제 날짜
        OffsetDateTime startOfDay = yesterday.toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();  // 어제 00:00:00
        OffsetDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);  // 어제 23:59:59.999999

        // 모든 매장 조회
        List<ShopEntity> shops = shopRepository.findAll();

        // 각 매장별로 처리
        for (ShopEntity shop : shops) {
            // 오늘 00:00:00부터 현재 시간까지 퇴장한 사람들만 조회
            List<EnterHistoryEntity> enterHistories = enterHistoryRepository.findByShopAndExitTimeBetween(
                    shop,
                    startOfDay,  // 어제 00:00:00
                    endOfDay  // 어제 23:59:59.999999
            );

            if (enterHistories.isEmpty()) {
                continue;
            }

            // 총 이용 시간을 계산
            int totalUsageMinutes = 0;

            // 좌석 ID별 누적 시간을 계산하기 위한 맵
            Map<Long, Integer> seatUsageDuration = new HashMap<>();

            // 유니크한 memberId를 구하기 위한 Set (중복을 제거하여 사용자 수를 셈)
            Set<Long> uniqueMemberIds = new HashSet<>();

            for (EnterHistoryEntity enterHistory : enterHistories) {
                OffsetDateTime exitTime = enterHistory.getExitTime();
                long usageTimeInMinutes = ChronoUnit.MINUTES.between(enterHistory.getEnterTime(), exitTime);
                totalUsageMinutes += (int) usageTimeInMinutes; // 총 이용 시간 누적
                SeatEntity seat = enterHistory.getSeat();
                Long seatId = seat.getId();
                seatUsageDuration.put(seatId, seatUsageDuration.getOrDefault(seatId, 0) + (int) usageTimeInMinutes);

                // 유니크한 사용자 ID (memberId) 추가
                if (enterHistory.getMember() != null) {
                    uniqueMemberIds.add(enterHistory.getMember().getId());
                }
            }

            // totalUsageUsers 계산 (유니크한 memberId 수)
            int totalUsageUsers = uniqueMemberIds.size();

            // UserAvrUsageEntity 객체 생성 후 저장
            UserAvrUsageEntity userAvrUsageEntity = UserAvrUsageEntity.builder()
                    .shop(shop)  // 해당 매장
                    .year(yesterday.getYear())  // 현재 연도
                    .month(yesterday.getMonthValue())  // 현재 월
                    .day(yesterday.getDayOfMonth())  // 현재 일
                    .totalUsageMinutes(totalUsageMinutes)  // 총 이용 시간
                    .totalUsageUsers(totalUsageUsers)  // 총 이용 사용자 수
                    .usageDate(yesterday.toLocalDate())
                    .averageUsageMinutes((double) totalUsageMinutes / totalUsageUsers)
                    .build();

            // 계산된 데이터를 DB에 저장
            userAvrUsageRepository.save(userAvrUsageEntity);

            // 좌석 ID별 누적 시간 처리
            saveSeatUsageStatistics(shop, yesterday, seatUsageDuration);
        }
    }


    /**
     * 좌석 ID별 누적 시간 처리 메서드
     */
    private void saveSeatUsageStatistics(ShopEntity shop, OffsetDateTime now, Map<Long, Integer> seatUsageDuration) {
        // 좌석 사용 시간 기록을 DB에 저장하는 로직 추가
        for (Map.Entry<Long, Integer> entry : seatUsageDuration.entrySet()) {
            Long seatId = entry.getKey();
            Integer usageDuration = entry.getValue();

            // SeatIdUsageEntity 객체 생성
            SeatIdUsageEntity seatIdUsageEntity = SeatIdUsageEntity.builder()
                    .shop(shop)
                    .year(now.getYear())
                    .month(now.getMonthValue())
                    .day(now.getDayOfMonth())
                    .dayOfWeek(now.getDayOfWeek())
                    .hour(now.getHour())
                    .usageDate(now.toLocalDate())
                    .seatUsageDuration(Map.of(seatId, usageDuration)) // 좌석 ID와 사용 시간
                    .build();

            // 좌석 사용 시간 기록 저장
            seatIdUsageRepository.save(seatIdUsageEntity);
        }
    }

    @Override
    @Transactional
    public void calculateAndSaveShopDailyPayment() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime yesterday = now.minusDays(1);  // 어제 날짜
        OffsetDateTime startOfDay = yesterday.toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();  // 어제 00:00:00
        OffsetDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);  // 어제 23:59:59.999999

        // 모든 매장 조회
        List<ShopEntity> shops = shopRepository.findAll();

        for (ShopEntity shop : shops) {
            // 어제 날짜의 결제 기록 조회
            List<TimeTicketHistoryEntity> timeTicketHistories = timeTicketHistoryRepository.findByShopAndPaymentDateBetween(shop, startOfDay, endOfDay);
            List<PeriodTicketHistoryEntity> periodTicketHistories = periodTicketHistoryRepository.findByShopAndPaymentDateBetween(shop, startOfDay, endOfDay);

            // 티켓 종류별 결제 금액 계산
            int totalAmountForTimeTicket = 0;
            int totalAmountForPeriodTicket = 0;

            // 시간권 결제액 합산
            for (TimeTicketHistoryEntity timeTicketHistory : timeTicketHistories) {
                totalAmountForTimeTicket += timeTicketHistory.getTicket().getAmount();
            }

            // 기간권 결제액 합산
            for (PeriodTicketHistoryEntity periodTicketHistory : periodTicketHistories) {
                totalAmountForPeriodTicket += periodTicketHistory.getTicket().getAmount();
            }

            // ShopDailyPaymentEntity 객체 생성 후 저장 (시간권)
            if (totalAmountForTimeTicket > 0) {
                ShopDailyPaymentEntity shopDailyPaymentForTime = ShopDailyPaymentEntity.builder()
                        .shop(shop) // 해당 매장
                        .year(yesterday.getYear()) // 연도
                        .month(yesterday.getMonthValue()) // 월
                        .day(yesterday.getDayOfMonth()) // 일
                        .dayOfWeek(yesterday.getDayOfWeek()) // 요일
                        .usageDate(yesterday.toLocalDate())
                        .totalAmount(totalAmountForTimeTicket) // 총 결제액
                        .ticketType(TicketTypeEnum.TIME) // 티켓 종류: 시간권
                        .build();
                // 계산된 데이터를 DB에 저장
                shopDailyPaymentRepository.save(shopDailyPaymentForTime);
            }

            // ShopDailyPaymentEntity 객체 생성 후 저장 (기간권)
            if (totalAmountForPeriodTicket > 0) {
                ShopDailyPaymentEntity shopDailyPaymentForPeriod = ShopDailyPaymentEntity.builder()
                        .shop(shop) // 해당 매장
                        .year(yesterday.getYear()) // 연도
                        .month(yesterday.getMonthValue()) // 월
                        .day(yesterday.getDayOfMonth()) // 일
                        .dayOfWeek(yesterday.getDayOfWeek()) // 요일
                        .usageDate(yesterday.toLocalDate())
                        .totalAmount(totalAmountForPeriodTicket) // 총 결제액
                        .ticketType(TicketTypeEnum.PERIOD) // 티켓 종류: 기간권
                        .build();
                // 계산된 데이터를 DB에 저장
                shopDailyPaymentRepository.save(shopDailyPaymentForPeriod);
            }
        }
    }

    @Override
    @Transactional
    // 고객 수를 저장하는 함수
    public void calculateAndSaveCustomerStats() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime yesterday = now.minusDays(1);  // 어제 날짜


        DayOfWeek dayOfWeek = now.getDayOfWeek();

        // 각 Shop별 고객 수 조회
        for (ShopEntity shop : shopRepository.findAll()) {
            int totalCustomers = memberRepository.countByShop(shop);

            // 해당 일자의 통계 저장
            CustomerChangeStatsEntity statsEntity = CustomerChangeStatsEntity.builder()
                    .shop(shop)
                    .year(yesterday.getYear())
                    .month(yesterday.getMonthValue())
                    .day(yesterday.getDayOfMonth())
                    .dayOfWeek(dayOfWeek)
                    .usageDate(yesterday.toLocalDate())
                    .totalCustomers(totalCustomers)
                    .build();

            customerChangeStatsRepository.save(statsEntity);
        }
    }


    @Override
    public FinalResponseDto<SeatIdUsageResponseDto> getSeatUsageEntitiesByDateRange(SeatIdUsageRequestDto requestDto) {
        LocalDate localStartDate = requestDto.getStartDate();
        LocalDate localEndDate = requestDto.getEndDate();
        // 연도, 월, 일로 분리하여 조회
        long shopId = requestDto.getShopId();
        System.out.println("getSeatUsageEntitiesByDateRange");
        System.out.println("Start Date: " + localStartDate);
        System.out.println("End Date: " + localEndDate);

        // 기간에 맞는 SeatIdUsageEntity 목록 조회
        List<SeatIdUsageEntity> seatIdUsageEntities = seatIdUsageRepository.findByShopIdAndUsageDateBetween(shopId, localStartDate, localEndDate);

        // SeatIdUsageEntity를 SeatIdUsageDto로 변환
        List<SeatIdUsageResponseDto.SeatIdUsageDto> seatIdUsageDtoList = seatIdUsageEntities.stream()
                .map(entity -> SeatIdUsageResponseDto.SeatIdUsageDto.builder()
                        .id(entity.getId()) // SeatIdUsageEntity의 id
                        .shopId(entity.getShop().getId()) // ShopEntity의 id
                        .seatUsageDuration(entity.getSeatUsageDuration()) // 좌석 ID별 사용 시간
                        .build())
                .collect(Collectors.toList());

        // 새로운 DTO로 응답 객체 생성
        SeatIdUsageResponseDto responseDto = SeatIdUsageResponseDto.builder()
                .seatIdUsageEntityList(seatIdUsageDtoList)
                .build();

        // 데이터가 없을 경우 처리
        if (seatIdUsageEntities.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        // 성공적으로 데이터를 반환
        return FinalResponseDto.successWithData(responseDto);
    }

    @Override
    public FinalResponseDto<List<ShopDailyPaymentResponseDto>> getShopDailyPaymentsByDateRange(ShopPaymentRequestDto requestDto) {
        LocalDate localStartDate = requestDto.getStartDate();
        LocalDate localEndDate = requestDto.getEndDate();


        // 연도, 월, 일로 분리하여 조회
        long shopId = requestDto.getShopId();
        System.out.println("getShopDailyPaymentsByDateRange");
        System.out.println("Start Date: " + localStartDate);
        System.out.println("End Date: " + localEndDate);


        List<ShopDailyPaymentEntity> shopDailyPaymentEntities = shopDailyPaymentRepository.findByShopIdAndUsageDateBetween(shopId, localStartDate, localEndDate);
        System.out.println("shopDailyPaymentEntities: " + shopDailyPaymentEntities.size());

        // 데이터가 없을 경우 처리
        if (shopDailyPaymentEntities.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        // ShopDailyPaymentEntity 목록을 ShopDailyPaymentResponseDto 목록으로 변환
        List<ShopDailyPaymentResponseDto> responseDtos = shopDailyPaymentEntities.stream()
                .map(ShopDailyPaymentResponseDto::fromEntity)
                .collect(Collectors.toList());

        // ShopDailyPaymentResponseDto를 포함한 응답 객체 반환
        return FinalResponseDto.successWithData(responseDtos);
    }


    @Override
    public FinalResponseDto<PaymentHistoryDto> getShopDailyPaymentsByDateRangeAndByName(ShopPaymentRequestIncludeNameDto requestDto) {
        // ShopPaymentRequestIncludeNameDto에서 startDate와 endDate를 추출하여 PaymentHistoryDateRequestDto 객체 생성
        PaymentHistoryDateRequestDto paymentHistoryDateRequestDto = PaymentHistoryDateRequestDto.fromShopPaymentRequestIncludeNameDto(requestDto);
        System.out.println("requestDto^^" + requestDto);
        // 이후 paymentHistoryDateRequestDto를 사용하여 결제 내역을 조회
        LocalDate localStartDate = paymentHistoryDateRequestDto.getStartDate();
        LocalDate localEndDate = paymentHistoryDateRequestDto.getEndDate();
        String userName = requestDto.getUserName();
        long shopId = requestDto.getShopId();

        // 사용자 조회
        MemberEntity member = memberRepository.findByNameAndShopId(userName, shopId);
        if (member == null) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);  // 사용자 없음
        }

        return ticketServiceImpl.getPaymentHistory(paymentHistoryDateRequestDto, shopId, member.getId());


    }


    @Override
    public FinalResponseDto<List<ShopUsageResponseDto>> getShopUsageByDateRange(ShopUsageRequestDto requestDto) {
        LocalDate localStartDate = requestDto.getStartDate();
        LocalDate localEndDate = requestDto.getEndDate();
        // 연도, 월, 일로 분리하여 조회
        long shopId = requestDto.getShopId();
        System.out.println("getShopUsageByDateRange");
        System.out.println("Start Date: " + localStartDate);
        System.out.println("End Date: " + localEndDate);

        // 기간에 맞는 shopDailyUsageEntities 목록 조회
        List<ShopUsageDailyEntity> shopDailyUsageEntities = shopUsageDailyRepository
                .findByShopIdAndUsageDateBetween(shopId, localStartDate, localEndDate);
        System.out.println("shopDailyPaymentEntities: " + shopDailyUsageEntities.size());

        // 데이터가 없을 경우 처리
        if (shopDailyUsageEntities.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }


        List<ShopUsageResponseDto> responseDtos = shopDailyUsageEntities.stream()
                .map(ShopUsageResponseDto::fromEntity)
                .collect(Collectors.toList());


        return FinalResponseDto.successWithData(responseDtos);
    }

    @Override
    public FinalResponseDto<List<UserAvrUsageResponseDto>> getUserAvrUsageByDateRange(UserAvrUsageRequestDto requestDto) {

        LocalDate localStartDate = requestDto.getStartDate();
        LocalDate localEndDate = requestDto.getEndDate();
        // 연도, 월, 일로 분리하여 조회
        long shopId = requestDto.getShopId();
        System.out.println("getUserAvrUsageByDateRange");
        System.out.println("Start Date: " + localStartDate);
        System.out.println("End Date: " + localEndDate);


        // 기간에 맞는 shopDailyUsageEntities 목록 조회
        List<UserAvrUsageEntity> userAvrUsageEntities = userAvrUsageRepository
                .findByShopIdAndUsageDateBetween(shopId, localStartDate, localEndDate);
        System.out.println("userAvrUsageEntities: " + userAvrUsageEntities.size());

        // 데이터가 없을 경우 처리
        if (userAvrUsageEntities.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }


        List<UserAvrUsageResponseDto> responseDtos = userAvrUsageEntities.stream()
                .map(UserAvrUsageResponseDto::fromEntity)
                .collect(Collectors.toList());


        return FinalResponseDto.successWithData(responseDtos);
    }

    @Override
    public FinalResponseDto<List<UserChangeStatsResponseDto>> getUserChangeStatsByDateRange(UserChangeStatsRequestDto requestDto) {

        LocalDate localStartDate = requestDto.getStartDate();
        LocalDate localEndDate = requestDto.getEndDate();
        // 연도, 월, 일로 분리하여 조회
        long shopId = requestDto.getShopId();
        System.out.println("getUserChangeStatsByDateRange");
        System.out.println("Start Date: " + localStartDate);
        System.out.println("End Date: " + localEndDate);
        // 기간에 맞는 shopDailyUsageEntities 목록 조회
        List<CustomerChangeStatsEntity> userChangeStatsEntities = customerChangeStatsRepository
                .findByShopIdAndUsageDateBetween(shopId, localStartDate, localEndDate);
        System.out.println("userChangeStatsEntities: " + userChangeStatsEntities.size());

        // 데이터가 없을 경우 처리
        if (userChangeStatsEntities.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }


        List<UserChangeStatsResponseDto> responseDtos = userChangeStatsEntities.stream()
                .map(UserChangeStatsResponseDto::fromEntity)
                .collect(Collectors.toList());


        return FinalResponseDto.successWithData(responseDtos);
    }

}
