package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.MemberSignInRequestDto;
import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.requestDto.ShopSignInRequestDto;
import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;
import com.example.studyroom.security.JwtCookieUtil;
import com.example.studyroom.security.JwtUtil;
import com.example.studyroom.type.ApiResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private final JwtUtil jwtUtil;
    private final PeriodTicketRepository periodTicketRepository;
    private final TimeTicketRepository timeTicketRepository;

    public ShopServiceImpl(ShopRepository repository,  MemberService memberService, SeatRepository seatRepository,
                           RoomRepository roomRepository, MemberServiceImpl memberServiceImpl,
                           EnterHistoryRepository enterHistoryRepository, MemberRepository memberRepository,
                           ShopRepository shopRepository, JwtUtil jwtUtil, PeriodTicketRepository periodTicketRepository, TimeTicketRepository timeTicketRepository) {
        super(repository);
        this.repository = repository;
        this.memberService = memberService;
        this.seatRepository = seatRepository;
        this.roomRepository = roomRepository;
        this.memberServiceImpl = memberServiceImpl;
        this.enterHistoryRepository = enterHistoryRepository;
        this.memberRepository = memberRepository;
        this.shopRepository = shopRepository;

        this.jwtUtil = jwtUtil;
        this.periodTicketRepository = periodTicketRepository;
        this.timeTicketRepository = timeTicketRepository;
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

        if (!timeTicketEntities.isEmpty()) {
            System.out.println("첫 번째 TimeTicketEntity: " + timeTicketEntities.get(0).getDays());
        } else {
            System.out.println("TimeTicketEntity 리스트가 비어 있습니다.");
        }
        ProductResponseDto productResponseDto = ProductResponseDto.builder()
                .periodTicketList(periodTicketEntities)
                .timeTicketList(timeTicketEntities)
                .build();
        System.out.println(productResponseDto);

        return FinalResponseDto.successWithData(productResponseDto);

    }









}
