package com.example.studyroom.repository;

import com.example.studyroom.model.ChatMessageEntity;
import org.springframework.data.domain.Page;

import com.example.studyroom.model.ChatRoomEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findByRoomOrderByTimestampAsc(ChatRoomEntity room);


    Optional<ChatMessageEntity> findTopByRoomOrderByTimestampDesc(ChatRoomEntity room);

    Page<ChatMessageEntity> findByRoom(ChatRoomEntity room, Pageable pageable);

}
