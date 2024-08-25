package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;
import com.example.studyroom.type.ApiResult;
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
    private final TicketHistoryRepository ticketHistoryRepository;
    private final TicketRepository ticketRepository;

    public ShopServiceImpl(ShopRepository repository,TicketRepository ticketRepository ,MemberService memberService, SeatRepository seatRepository, RoomRepository roomRepository, MemberServiceImpl memberServiceImpl, EnterHistoryRepository enterHistoryRepository, MemberRepository memberRepository, ShopRepository shopRepository, TicketHistoryRepository ticketHistoryRepository) {
        super(repository);
        this.repository = repository;
        this.memberService = memberService;
        this.seatRepository = seatRepository;
        this.roomRepository = roomRepository;
        this.memberServiceImpl = memberServiceImpl;
        this.enterHistoryRepository = enterHistoryRepository;
        this.memberRepository = memberRepository;
        this.shopRepository = shopRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.ticketRepository=ticketRepository;
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
//        return FinalResponseDto.builder()
//                .message("정보가 성공적으로 반환되었습니다")
//                .statusCode("0000")
//                .data(this.findAll())
//                .build();

    }

//    @Override
//    //위에서 받은 리스트를 ShopListResponseDto로바꾸고싶음
//    public List<ShopListResponseDto> getShopListResponseDto(Long shopId) {
//        // Shop Id 기준으로 멤버를 찾는다. (특정 지점의 고객)
//        List<MemberEntity> members = getMemberList(shopId);
//        //
//        return members.stream()
//                .map(member -> ShopListResponseDto.builder()
//                        .shopId(member.getShop().getId())
//                        .name(member.getName())
//                        .build())
//                .collect(Collectors.toList());
//    }

    @Override //로그인
    public FinalResponseDto<ShopEntity> login(String email, String password) {
        //레포지토리에있는 함수가져오기
        //
        ShopEntity Shop = repository.findByEmailAndPassword(email, password);

        if (Shop != null) {
            // 점주 존재하면 로그인 성공
            return FinalResponseDto.successWithData(Shop);
//            return FinalResponseDto.builder()
//                    .message("정보가 성공적으로 반환되었습니다")
//                    .statusCode("0000")
//                    .data(Shop)
//                    .build();

           // return Shop;
        } else {
            return FinalResponseDto.failure(ApiResult.AUTHENTICATION_FAILED);
//            return FinalResponseDto.builder()
//                    .message("로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.")
//                    .statusCode("3000")
//                    .build();
            //throw new RuntimeException("로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
    }


    @Override //회원가입
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


//        ShopEntity shop = repository.findById(shopId)
//                .orElse(new ShopEntity());

//        ShopEntity shop = repository.findById(shopId)
//                .orElse(null);


        ShopInfoResponseDto shopInfo = ShopInfoResponseDto.builder()
                .location(shop.get().getLocation()) //TODO:이거에러 어떻게없애지 질문?
                .name(shop.get().getName())
                .build();

        return FinalResponseDto.successWithData(shopInfo);

//        return FinalResponseDto.<ShopInfoResponseDto>builder()
//                .message("정보가 성공적으로 반환되었습니다")
//                .statusCode("0000")
//                .data(shopInfo)
//                .build();


    }

    @Override
    public FinalResponseDto<List<RoomAndSeatInfoResponseDto>> getRoomsAndSeatsByShopId(Long shopId, Long customerId) {
        // EnterHistoryService를 사용하여 현재 사용자의 좌석 ID를 조회
        Long mySeatId = memberServiceImpl.getSeatIdByCustomerId(customerId);

        // Shop ID로 방 목록을 조회
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
//        return FinalResponseDto.builder()
//                .message("정보가 성공적으로 반환되었습니다.")
//                .statusCode("0000")
//                .data(roomAndSeatInfoDtos)
//                .build();
    }

    //누가 자리점유요청 메시지창만 띄워도 점유가 되게 하고 다른 자리를 점유하면 그 자리는 점유를 풀기(어떻게하지?)->일단 보류

    @Override
    public FinalResponseDto<List<ProductResponseDto>> getProductList(Long shopId ,String productType){
        List<TicketEntity> tickets = ticketRepository.findByShopIdAndType(shopId,productType);
//        if(!ObjectUtils.isEmpty(tickets)) {
////        if(!tickets.isEmpty() ){
//            List<ProductResponseDto> productResponseDto = tickets.stream()
//                    .map(ticket -> ProductResponseDto.builder()
//                            .productId(ticket.getId())
//                            .name(ticket.getName())
//                            .amount(ticket.getAmount())
//                            .period(ticket.getPeriod())
//                            .type(ticket.getType())
//                            .build())
//                    .collect(Collectors.toList());
//
//            return ProductListResponseDto.builder()
//                    .productInfo(productResponseDto)
//                    .build();
//        }
//        return MessageResponseDto.builder()
//                .message("잘못된 샵정보")
//                .statusCode("3000")
//                .build();

        List<ProductResponseDto> productResponseDto = tickets.stream()
                .map(ticket -> ProductResponseDto.builder()
                        .productId(ticket.getId())
                        .name(ticket.getName())
                        .amount(ticket.getAmount())
                        .period(ticket.getPeriod())
                        .type(ticket.getType())
                        .build())
                .collect(Collectors.toList());


        return FinalResponseDto.successWithData(productResponseDto);
//        return FinalResponseDto.<List<ProductResponseDto>>builder()
//                .message("정보가 성공적으로 반환되었습니다.")
//                .statusCode("0000")
//                .data(productResponseDto)
//                .build();
    }


}
