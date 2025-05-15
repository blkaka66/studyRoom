package com.example.studyroom.repository;

import com.example.studyroom.model.TicketExpirationAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketExpirationAlertRepository extends JpaRepository<TicketExpirationAlertEntity, Long> {

    Optional<TicketExpirationAlertEntity> findFirstByMemberIdAndShopIdAndTicketTypeAndSentFalse(
            Long memberId,
            Long shopId,
            String ticketType
    );

    List<TicketExpirationAlertEntity> findBySendTimeBeforeAndSentFalse(OffsetDateTime now);

}
