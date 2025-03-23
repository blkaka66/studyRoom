package com.example.studyroom.repository;

import com.example.studyroom.model.SeatEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findByRoomId(Long roomId);

    Optional<SeatEntity> findBySeatCodeAndRoom_Id(int seatCode, Long roomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatEntity s WHERE s.seatCode = :seatCode AND s.room.id = :roomId")
    Optional<SeatEntity> findBySeatCodeAndRoom_IdWithPessimisticLock(@Param("seatCode") int seatCode, @Param("roomId") Long roomId);

}
