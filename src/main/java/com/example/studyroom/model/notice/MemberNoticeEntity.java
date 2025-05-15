package com.example.studyroom.model.notice;

import com.example.studyroom.model.BaseEntity;
import com.example.studyroom.model.MemberEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "member_notice")
public class MemberNoticeEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "FK_MEMBER_ID"))
    private MemberEntity member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeType noticeType;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;  // 생성 날짜

    @Column(nullable = false)
    private Boolean isRead = false;

    @Builder
    public MemberNoticeEntity(MemberEntity member, String title, String content, NoticeType noticeType, OffsetDateTime createdAt) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.noticeType = noticeType;
        this.createdAt = createdAt;
    }

    public MemberNoticeEntity() {
    }
}
