package com.example.studyroom.repository;

import com.example.studyroom.model.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    // 사용자와 파트너 기준으로 채팅방 조회
    Optional<ChatRoomEntity> findByUserIdAndUserTypeAndPartnerIdAndPartnerType(Long userId, String userType, Long partnerId, String partnerType);

    //내가속한 채팅방조회
    @Query("SELECT c FROM ChatRoomEntity c WHERE (c.userId = :id AND c.userType = :type) OR (c.partnerId = :id AND c.partnerType = :type)")
    List<ChatRoomEntity> findRoomsByUserOrPartner(@Param("id") Long id, @Param("type") String type);


}
