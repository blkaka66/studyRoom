package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.NotificationResponseDto;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.notice.NoticeType;
import com.example.studyroom.model.notice.ShopNoticeEntity;

import java.time.OffsetDateTime;
import java.util.List;

public interface NoticeService {


    void saveShopNotice(ShopEntity shop, String title, String content, NoticeType noticeType, OffsetDateTime createdAt);

    FinalResponseDto<List<NotificationResponseDto>> getNotifications(long shopId);
}
