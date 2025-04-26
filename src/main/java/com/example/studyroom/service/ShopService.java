package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.EnterHistoryEntity;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.statistics.SeatIdUsageEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShopService extends BaseService<ShopEntity> {
    boolean existsByEmail(String email);

    // List<MemberEntity> getMemberList(Long shopId);
    FinalResponseDto<List<MemberResponseDto>> getMemberListAndInfo(Long shopId);

    //List<ShopEntity> getShopList();
    FinalResponseDto getShopList();
//    List<ShopListResponseDto> getShopListResponseDto(Long shopId);

    //ShopEntity login(String username, String password);
    FinalResponseDto<String> login(ShopSignInRequestDto dto, HttpServletResponse response);

    //로그아웃
    FinalResponseDto<String> logout(ShopEntity shop, String accessToken);


    //ShopEntity signUp(ShopSignUpRequestDto dto); //회원가입
    FinalResponseDto<ShopEntity> signUp(ShopSignUpRequestDto dto); //회원가입

    //ShopInfoResponseDto getShopInfo(Long shopId);
    FinalResponseDto<ShopInfoResponseDto> getShopInfo(Long shopId);


    //List<RoomAndSeatInfoResponseDto> getRoomsAndSeatsByShopId(Long shopId,Long customerId); //shopid,customerId받아서 방이랑 좌석정보 가져오기
    FinalResponseDto getRoomsAndSeatsByShopId(Long customerId); //customerId받아서 방이랑 좌석정보 가져오기

    FinalResponseDto<ProductResponseDto> getProductList(Long shopId);//티켓 정보가져오기(시간권 기간권나눠서)

    FinalResponseDto<String> createAnnounement(Long shopId, CreateAnnouncementRequestDto dto);

    FinalResponseDto<String> forceDeleteUser(ForceDeleteUserRequestDto dto);

    FinalResponseDto<List<AnnouncementResponseDto>> getAnnouncementList(Long shopId);

    FinalResponseDto<AnnouncementResponseDto> getAnnouncementInfo(Long docsId);

    FinalResponseDto<CouponInfoResponseDto> getCouponInfo(String couponCode, Long shopId);

    FinalResponseDto<List<SeatUsageStatsResponseDto>> getSeatUsageStats(Long shopId);


    void calculateAndSaveshopUsageHourly();

    void calculateAndSaveDailyOccupancy();

    //void calculateDailySeatUsageRate();

    void calculateAndSaveUsageStatistics();

    void calculateAndSaveShopDailyPayment();

    void calculateAndSaveCustomerStats();


    FinalResponseDto<SeatIdUsageResponseDto> getSeatUsageEntitiesByDateRange(SeatIdUsageRequestDto requestDto);

    FinalResponseDto<List<ShopDailyPaymentResponseDto>> getShopDailyPaymentsByDateRange(ShopPaymentRequestDto requestDto);

    FinalResponseDto<PaymentHistoryDto> getShopDailyPaymentsByDateRangeAndByName(ShopPaymentRequestIncludeNameDto requestDto);


    FinalResponseDto<List<ShopUsageResponseDto>> getShopUsageByDateRange(ShopUsageRequestDto requestDto);

    FinalResponseDto<List<UserAvrUsageResponseDto>> getUserAvrUsageByDateRange(UserAvrUsageRequestDto requestDto);

    FinalResponseDto<List<UserChangeStatsResponseDto>> getUserChangeStatsByDateRange(UserChangeStatsRequestDto requestDto);
}
