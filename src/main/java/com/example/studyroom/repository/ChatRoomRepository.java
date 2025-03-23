package com.example.studyroom.repository;

import com.example.studyroom.model.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    Optional<ChatRoomEntity> findByUserIdAndUserTypeAndPartnerIdAndPartnerType(
            Long userId, String userType, Long partnerId, String partnerType
    );

    List<ChatRoomEntity> findByUserIdAndUserType(Long userId, String userType);

}
