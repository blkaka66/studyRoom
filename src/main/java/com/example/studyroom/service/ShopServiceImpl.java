package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.RoomAndSeatInfoResponseDto;
import com.example.studyroom.dto.responseDto.SeatinfoResponseDto;
import com.example.studyroom.dto.responseDto.ShopInfoResponseDto;
import com.example.studyroom.dto.responseDto.ShopListResponseDto;
import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

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

    public ShopServiceImpl(ShopRepository repository, MemberService memberService, SeatRepository seatRepository, RoomRepository roomRepository, MemberServiceImpl memberServiceImpl, EnterHistoryRepository enterHistoryRepository, MemberRepository memberRepository, ShopRepository shopRepository, TicketHistoryRepository ticketHistoryRepository) {
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
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public List<MemberEntity> getMemberList(Long shopId) {
        Optional<ShopEntity> shop = this.findById(shopId);
        if(shop.isPresent()) {
//            return this.memberService.findByShop(shop.get());
            return shop.get().getMembers();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Override //지점목록 가져오기
    public List<ShopEntity> getShopList() {//shopId가 안들어오면 모든 리스트를 보내고 shopid가 들어오면 해당 shop리스트만 보내고
        return this.findAll();
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
    public ShopEntity login(String email, String password) {
        //레포지토리에있는 함수가져오기
        //
        ShopEntity Shop = repository.findByEmailAndPassword(email, password);

        if (Shop != null) {
            // 점주 존재하면 로그인 성공
            return Shop;
        } else {
            throw new RuntimeException("로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
    }


    @Override //회원가입
    public ShopEntity signUp(ShopSignUpRequestDto dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        ShopEntity shop = dto.toEntity();
        return repository.save(shop);
    }

    @Override // 지점정보가져오기
    public ShopInfoResponseDto getShopInfo(Long shopId) {
        if (shopId ==null) {
            throw new RuntimeException("존재하지않는 id");
        }
        ShopEntity shop = repository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 지점입니다."));

//        ShopEntity shop = repository.findById(shopId)
//                .orElse(new ShopEntity());

//        ShopEntity shop = repository.findById(shopId)
//                .orElse(null);

        ShopInfoResponseDto shopInfo = ShopInfoResponseDto.builder()
                .location(shop.getLocation())
                .name(shop.getName())
                .build();

        return shopInfo;
    }

    @Override
    public List<RoomAndSeatInfoResponseDto> getRoomsAndSeatsByShopId(Long shopId, Long customerId) {
        // EnterHistoryService를 사용하여 현재 사용자의 좌석 ID를 조회
        Long mySeatId = memberServiceImpl.getSeatIdByCustomerId(customerId);

        // Shop ID로 방 목록을 조회
        List<RoomEntity> rooms = roomRepository.findByShopId(shopId);

        // 방 목록을 DTO로 변환
        return rooms.stream().map(room -> {
            // 각 방에 대한 좌석 목록을 조회
//            List<SeatEntity> seats = seatRepository.findByRoomId(room.getId());
            // 좌석 목록을 DTO로 변환
//            List<SeatinfoResponseDto> seatDtos = seats.stream().map(seat -> {
            List<SeatinfoResponseDto> seatDtos = room.getSeats().stream().map(seat -> {
                // 현재 사용자의 좌석 ID와 mySeatId가 같으면 true 아니면 false
//                boolean isMySeat = (mySeatId != null && mySeatId.equals(seat.getId()));
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
    }

    //누가 자리점유요청 메시지창만 띄워도 점유가 되게 하고 다른 자리를 점유하면 그 자리는 점유를 풀기(어떻게하지?)


    @Override
    public boolean occupySeat(Long shopId , String roomName, int seatCode, Long memberId, Long ticketHistoryId) { //tickethistory레포지토리에서 회원id받아서 id받아오는 메서드추가해야함
        Optional<ShopEntity> shopOpt = shopRepository.findById(shopId);
        if(shopOpt.isPresent()){
            Optional<RoomEntity> roomOpt = roomRepository.findByName(roomName);
            if (roomOpt.isPresent()) {
                Long roomId = roomOpt.get().getId();
                Optional<SeatEntity> seatOpt = seatRepository.findBySeatCodeAndRoom_Id(seatCode, roomId);
                if (seatOpt.isPresent()) {
                    SeatEntity seat = seatOpt.get();
                    if (seat.getAvailable()) {
                        seat.setAvailable(false);
                        seatRepository.save(seat); //점유요청들어오면 seat abilable false로 바꾸기

                        Optional<MemberEntity> memberOpt = memberRepository.findById(memberId);
                        Optional<TicketHistoryEntity> ticketHistoryOpt = ticketHistoryRepository.findByShopIdAndUserId(shopId,memberId);

                        if (memberOpt.isPresent() && ticketHistoryOpt.isPresent()) {//enterhistory 만들기(closetime뺴고 다채우기)
                            MemberEntity member = memberOpt.get();
                            TicketHistoryEntity ticketHistory = ticketHistoryOpt.get();
                            OffsetDateTime now = OffsetDateTime.now();
                            OffsetDateTime expiredTime = ticketHistory.getExpiredTime();

                            EnterHistoryEntity enterHistory = new EnterHistoryEntity(member, seat, ticketHistory, now, expiredTime);
                            enterHistoryRepository.save(enterHistory);
                        }

                        return true;
                    }
                }
            }
            return false;
        }
        return false;

    }

}
