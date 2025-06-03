package com.example.studyroom.repository;

import com.example.studyroom.model.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {


    //내가속한 채팅방조회
    @Query("SELECT c FROM ChatRoomEntity c WHERE (c.senderId = :id AND c.senderType = :type) OR (c.partnerId = :id AND c.partnerType = :type)")
    List<ChatRoomEntity> findRoomsBySenderOrPartner(@Param("id") Long id, @Param("type") String type);

//
//    //두 사용자 조합으로 중복 체크
//    @Query("SELECT r FROM ChatRoomEntity r " +
//            "WHERE (r.senderId = :senderId AND r.senderType = :senderType AND r.partnerId = :partnerId AND r.partnerType = :partnerType) " +
//            "   OR (r.senderId = :partnerId AND r.senderType = :partnerType AND r.partnerId = :senderId AND r.partnerType = :senderType)")
//    Optional<ChatRoomEntity> findChatRoomBidirectional(Long senderId, String senderType, Long partnerId, String partnerType);


    //두 사용자 조합으로 중복체크 + 둘다 나가지않은 방 체크
    @Query("SELECT r FROM ChatRoomEntity r " +
            "WHERE ((r.senderId = :senderId AND r.senderType = :senderType AND r.partnerId = :partnerId AND r.partnerType = :partnerType) " +
            "   OR (r.senderId = :partnerId AND r.senderType = :partnerType AND r.partnerId = :senderId AND r.partnerType = :senderType)) " +
            "AND r.senderClosed = false AND r.partnerClosed = false")
    Optional<ChatRoomEntity> findLatestActiveRoomBidirectional(
            Long senderId, String senderType,
            Long partnerId, String partnerType);


//    @Query("SELECT r FROM ChatRoomEntity r " +
//            "WHERE ((r.senderId = :senderId AND r.senderType = :senderType AND r.partnerId = :partnerId AND r.partnerType = :partnerType) " +
//            "   OR (r.senderId = :partnerId AND r.senderType = :partnerType AND r.partnerId = :senderId AND r.partnerType = :senderType))")
//    Optional<ChatRoomEntity> findRoomBidirectionalWithoutClosedCheck(
//            Long senderId, String senderType,
//            Long partnerId, String partnerType
//    );
}
