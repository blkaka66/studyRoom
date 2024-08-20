//package com.example.studyroom.repository;
//
//import com.example.studyroom.model.SeatEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
//    List<SeatEntity> findByRoomId(Long roomId);
//
//    Optional<SeatEntity> findBySeatCodeAndRoom_Id(int seatCode, Long roomId);
//}
