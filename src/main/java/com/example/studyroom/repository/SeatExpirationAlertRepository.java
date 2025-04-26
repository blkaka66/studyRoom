package com.example.studyroom.repository;

import com.example.studyroom.model.SeatExpirationAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

public interface SeatExpirationAlertRepository extends JpaRepository<SeatExpirationAlertEntity, Long> {
    List<SeatExpirationAlertEntity> findBySendTimeBeforeAndSentFalse(OffsetDateTime now);

    // 10분 알림 아직 안 보낸 것 삭제
    @Modifying
    @Transactional
    void deleteByMemberIdAndSentFalse(Long memberId);

}
