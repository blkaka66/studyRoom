package com.example.studyroom.repository;

import com.example.studyroom.model.EnterHistoryEntity;
import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface EnterHistoryRepository extends JpaRepository<EnterHistoryEntity, Long> {


    // 특정 회원의 현재 활성화된 입장 기록을 조회하는 메서드
    // 조건: customerId가 일치하고, expiredTime이 현재 시간 이전이며, closeTime이 null인 레코드
    @Query("SELECT e FROM EnterHistoryEntity e WHERE e.member.id = :customerId AND e.exitTime IS NULL")
    EnterHistoryEntity findActiveByCustomerId(@Param("customerId") Long customerId);//쿼리조건 추가할거면 이름에 active라고 추가해야함

    // 지정된 상점 ID와 일치하는 기록
    // && 입장 시간이 현재 시간 이전인 기록
    // &&퇴장 시간 null인 (아직 퇴장하지 않은 경우) 기록
    @Query("SELECT COUNT(e) FROM EnterHistoryEntity e " +
            "WHERE e.shop.id = :shopId " +
            "AND e.enterTime <= :currentTime " +
            "AND e.exitTime IS NULL")
    int countActiveEntriesByShopId(@Param("shopId") Long shopId, @Param("currentTime") OffsetDateTime currentTime);


    // 하루에 이용한 사용자 수(memberid로 구분)
    @Query("SELECT COUNT(DISTINCT e.member.id) FROM EnterHistoryEntity e WHERE e.shop.id = :shopId AND e.enterTime BETWEEN :startOfDay AND :endOfDay")
    int countUniqueUsersByShopIdAndDate(@Param("shopId") Long shopId,
                                        @Param("startOfDay") OffsetDateTime startOfDay,
                                        @Param("endOfDay") OffsetDateTime endOfDay);

    //현재 이용중인 자리 좌석 id별로 가져오기
    @Query("SELECT DISTINCT e.seat.id " +
            "FROM EnterHistoryEntity e " +
            "WHERE e.exitTime IS NULL " +
            "AND e.shop.id = :shopId")
    List<Long> findActiveOccupiedSeatIdsByShop(@Param("shopId") Long shopId);




    List<EnterHistoryEntity> findByShop_Id(Long shopId);


    // Shop과 ExitTime 사이에 해당하는 엔티티를 조회하는 메서드
    List<EnterHistoryEntity> findByShopAndExitTimeBetween(
            ShopEntity shop,
            OffsetDateTime startExitTime,
            OffsetDateTime endExitTime
    );
}
