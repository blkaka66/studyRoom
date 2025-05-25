package com.example.studyroom.repository.notice;


import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.notice.MemberNoticeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberNoticeRepository extends JpaRepository<MemberNoticeEntity, Long> {
    List<MemberNoticeEntity> findByMemberIdAndIsReadFalse(Long memberId);

    //List<MemberNoticeEntity> findByMemberIdAndIsReadFalseOrderByCreatedAtDesc(Long memberId);

    List<MemberNoticeEntity> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    MemberNoticeEntity findByMemberIdAndId(Long memberId, Long id);

    MemberNoticeEntity findByMemberAndId(MemberEntity memberEntity, Long id);

//    @Modifying(clearAutomatically = true)
//    @Query("delete from MemberNoticeEntity  m where m.member.id = :memberId and m.id =:noticeId")
//    int deleteByMemberIdAndNoticeId(@Param("memberId") Long memberId, @Param("noticeId") Long noticeId);
}
