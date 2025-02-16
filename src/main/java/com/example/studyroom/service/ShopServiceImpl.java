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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    , ShopDailyPaymentRepository shopDailyPaymentRepository, CustomerChangeStatsRepository customerChangeStatsRepository) {
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
    }


    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public FinalResponseDto<List<MemberResponseDto>> getMemberList(Long shopId) {
        Optional<ShopEntity> shop = this.findById(shopId);
        if(shop.isPresent()) {
            // TODO: of 등 사용해서 변경
            return FinalResponseDto.successWithData(MemberResponseDto.of(shop.get().getMembers()));
        }
        else {
            return FinalResponseDto.failureWithData(ApiResult.DATA_NOT_FOUND,new ArrayList<>());
            //return new ArrayList<>();
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

//    @Override //로그인
//    public FinalResponseDto<String> logout(MemberEntity member) {
//
//        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(member.getId());
//        if(enterHistory != null && enterHistory.getExitTime()==null) { //따로 자리퇴장요청을 하지않고 바로 로그아웃했을땐 자리를 빼야하니까
//            FinalResponseDto<String> outResponse = memberService.out(member.getId()); // out 메서드를 호출하여 좌석 퇴장 처리
//
//            if (outResponse.getMessage().equals(ApiResult.DATA_NOT_FOUND.name())) {
//                return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
//            }
//
//
//        }
//        String refreshTokenKey = "refreshToken:" + member.getId();  // 해당 사용자에 해당하는 refreshToken Redis 키
//        redisService.deleteValue(refreshTokenKey);
//
//        return FinalResponseDto.success(); // 로그아웃 성공
//
//    }



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
        if (shopId ==null) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
            //throw new RuntimeException("존재하지않는 id");
        }
        Optional<ShopEntity> shop = repository.findById(shopId);
        if(shop.isEmpty()){
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
    public FinalResponseDto<List<RoomAndSeatInfoResponseDto>> getRoomsAndSeatsByShopId( Long customerId) {
        // EnterHistoryService를 사용하여 현재 사용자의 좌석 ID를 조회
        Long mySeatId = memberServiceImpl.getSeatIdByCustomerId(customerId);
        System.out.println("mySeatId"+mySeatId);
        Optional<MemberEntity> member = memberRepository.findById(customerId);
        // Shop ID로 방 목록을 조회
        if(member.isEmpty()){
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        Long shopId = member.get().getShop().getId();
        System.out.println("shopId"+shopId);
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
    public FinalResponseDto <ProductResponseDto> getProductList(Long shopId){

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
    public FinalResponseDto <String> createAnnounement(Long shopId, CreateAnnouncementRequestDto dto){

        Optional<ShopEntity> shopOptional = shopRepository.findById(shopId);
        if (shopOptional.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.SHOP_NOT_FOUND);
        }
        System.out.println("제목"+dto.getTitle());
        System.out.println("본문"+dto.getContent());
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
    public FinalResponseDto<CouponInfoResponseDto> getCouponInfo(String couponCode,Long shopId) {
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
        OffsetDateTime startOfDay = now.toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();  // 오늘 00:00:00
        OffsetDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);  // 오늘 23:59:59.999999

        List<ShopEntity> shops = shopRepository.findAll();
        for (ShopEntity shop : shops) {
            // 해당 날짜에 해당 상점에서 이용한 사용자 수 카운트
            int uniqueUsersCount = enterHistoryRepository.countUniqueUsersByShopIdAndDate(shop.getId(), startOfDay, endOfDay);

            // 일일 통계 저장
            ShopUsageDailyEntity dailyOccupancy = ShopUsageDailyEntity.from(shop, now, uniqueUsersCount);
            shopUsageDailyRepository.save(dailyOccupancy);
        }
    }



    @Override
    @Transactional
    public void calculateAndSaveUsageStatistics() {
        // 현재 날짜와 시간을 구합니다.
        OffsetDateTime now = OffsetDateTime.now();
        // 오늘 00:00:00 (자정)
        OffsetDateTime startOfToday = now.toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime();

        // 모든 매장 조회
        List<ShopEntity> shops = shopRepository.findAll();

        // 각 매장별로 처리
        for (ShopEntity shop : shops) {
            // 오늘 00:00:00부터 현재 시간까지 퇴장한 사람들만 조회
            List<EnterHistoryEntity> enterHistories = enterHistoryRepository.findByShopAndExitTimeBetween(
                    shop,
                    startOfToday,  // 오늘 00:00:00
                    now  // 현재 시각
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
                    .year(now.getYear())  // 현재 연도
                    .month(now.getMonthValue())  // 현재 월
                    .day(now.getDayOfMonth())  // 현재 일
                    .totalUsageMinutes(totalUsageMinutes)  // 총 이용 시간
                    .totalUsageUsers(totalUsageUsers)  // 총 이용 사용자 수
                    .averageUsageMinutes((double) totalUsageMinutes / totalUsageUsers)
                    .build();

            // 계산된 데이터를 DB에 저장
            userAvrUsageRepository.save(userAvrUsageEntity);

            // 좌석 ID별 누적 시간 처리
            saveSeatUsageStatistics(shop, now, seatUsageDuration);
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
                    .seatUsageDuration(Map.of(seatId, usageDuration)) // 좌석 ID와 사용 시간
                    .build();

            // 좌석 사용 시간 기록 저장
            seatIdUsageRepository.save(seatIdUsageEntity);
        }
    }

    @Override
    @Transactional
    public void calculateAndSaveShopDailyPayment() {
        OffsetDateTime now = OffsetDateTime.now(); // 현재 시각 (OffsetDateTime)
        OffsetDateTime startOfToday = now.toLocalDate().atStartOfDay(now.getOffset()).toOffsetDateTime(); // 오늘 자정 (OffsetDateTime)
        OffsetDateTime endOfToday = startOfToday.plusDays(1).minusSeconds(1); // 오늘 23:59:59 (OffsetDateTime)

        // 모든 매장 조회
        List<ShopEntity> shops = shopRepository.findAll();

        for (ShopEntity shop : shops) {
            // 오늘 날짜의 결제 기록 조회
            List<TimeTicketHistoryEntity> timeTicketHistories = timeTicketHistoryRepository.findByShopAndPaymentDateBetween(shop, startOfToday, now);
            List<PeriodTicketHistoryEntity> periodTicketHistories = periodTicketHistoryRepository.findByShopAndPaymentDateBetween(shop, startOfToday, now);

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
                        .year(now.getYear()) // 연도
                        .month(now.getMonthValue()) // 월
                        .day(now.getDayOfMonth()) // 일
                        .dayOfWeek(now.getDayOfWeek()) // 요일
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
                        .year(now.getYear()) // 연도
                        .month(now.getMonthValue()) // 월
                        .day(now.getDayOfMonth()) // 일
                        .dayOfWeek(now.getDayOfWeek()) // 요일
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
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        // 각 Shop별 고객 수 조회
        for (ShopEntity shop : shopRepository.findAll()) {
            int totalCustomers = memberRepository.countByShop(shop);

            // 해당 일자의 통계 저장
            CustomerChangeStatsEntity statsEntity = CustomerChangeStatsEntity.builder()
                    .year(now.getYear())
                    .month(now.getMonthValue())
                    .day(now.getDayOfMonth())
                    .dayOfWeek(dayOfWeek)
                    .totalCustomers(totalCustomers)
                    .build();

            customerChangeStatsRepository.save(statsEntity);
        }
    }

    @Override
// 기간에 맞는 SeatIdUsageEntity 목록 조회
    public FinalResponseDto<SeatIdUsageResponseDto> getSeatUsageEntitiesByDateRange(SeatIdUsageRequestDto requestDto) {
        OffsetDateTime startDate = requestDto.getStartDate(); // OffsetDateTime으로 받아온다.
        OffsetDateTime endDate = requestDto.getEndDate(); // OffsetDateTime으로 받아온다.
        System.out.println("startDate" + startDate);
        System.out.println("endDate" + endDate);

        // 연도, 월, 일로 분리하여 조회 (OffsetDateTime에서 연도, 월, 일 추출)
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();
        int startMonth = startDate.getMonthValue();
        int endMonth = endDate.getMonthValue();
        int startDay = startDate.getDayOfMonth();
        int endDay = endDate.getDayOfMonth();

        long shopId = requestDto.getShopId();
        System.out.println("Start Date: " + startYear + "-" + startMonth + "-" + startDay);
        System.out.println("End Date: " + endYear + "-" + endMonth + "-" + endDay);

        // 기간에 맞는 SeatIdUsageEntity 목록 조회
        List<SeatIdUsageEntity> seatIdUsageEntities = seatIdUsageRepository.findByShopIdAndYearBetweenAndMonthBetweenAndDayBetween(shopId, startYear, endYear, startMonth, endMonth, startDay, endDay);
        SeatIdUsageResponseDto responseDto = SeatIdUsageResponseDto.builder()
                .seatIdUsageEntityList(seatIdUsageEntities)
                .build();

        // 데이터가 없을 경우 처리
        if (seatIdUsageEntities.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        // 성공적으로 데이터를 반환
        return FinalResponseDto.successWithData(responseDto);
    }

}
