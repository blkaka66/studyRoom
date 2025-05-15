package com.example.studyroom.repository;


import com.example.studyroom.model.notice.MemberNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberNoticeRepository extends JpaRepository<MemberNoticeEntity, Long> {
    List<MemberNoticeEntity> findByMemberIdAndIsReadFalse(Long memberId);

    List<MemberNoticeEntity> findByMemberIdAndIsReadFalseOrderByCreatedAtDesc(Long memberId);
}
