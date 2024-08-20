//package com.example.studyroom.service;
//
//import com.example.studyroom.dto.responseDto.FinalResponseDto;
//import com.example.studyroom.dto.responseDto.RemainTimeResponseDto;
//import com.example.studyroom.model.EnterHistoryEntity;
//import com.example.studyroom.model.MemberEntity;
//import com.example.studyroom.model.ShopEntity;
//
//import java.util.List;
//
//public interface MemberService extends BaseService<MemberEntity> {
//    List<MemberEntity> findByShop(ShopEntity shop);
//
//
//
//    // 로그인했을때 남아있는 티켓시간 가져오는 메서드 (시간권이면 remainTime, 기간권이면 endTime 가져오기(쿠키나 토큰에서 id들 뽑아올예정))
////    Duration getRemainTime(Long shopId, Long userId);
//    FinalResponseDto getRemainTime(Long shopId, Long userId);
//
//    EnterHistoryEntity getSeatId(Long userId); //회원id받아서 어떤자리에 앉았는지 추출
//
//    MemberEntity login(String phone, String password); //로그인 기능
//
//    FinalResponseDto occupySeat(Long shopId , String roomName, int seatCode, Long memberId); //자리 점유요청 메서드
//
//    FinalResponseDto out(Long userId);//퇴장
//
//    FinalResponseDto move(Long userId, Long movingRoomCode, int movingSeatNumber);//자리이동
//
//
//}
