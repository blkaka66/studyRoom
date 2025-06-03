package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.NotificationResponseDto;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.notice.MemberNoticeEntity;
import com.example.studyroom.model.notice.NoticeType;
import com.example.studyroom.model.notice.ShopNoticeEntity;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.repository.notice.ShopNoticeRepository;
import com.example.studyroom.type.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {
    private final ShopNoticeRepository shopNoticeRepository;
    private final ShopRepository shopRepository;

    public NoticeServiceImpl(ShopNoticeRepository shopNoticeRepository, ShopRepository shopRepository) {
        this.shopNoticeRepository = shopNoticeRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public void saveShopNotice(ShopEntity shop, String title, String content, NoticeType noticeType, OffsetDateTime createdAt) {

        ShopNoticeEntity shopNotice = ShopNoticeEntity.builder()
                .shop(shop)
                .title(title)
                .content(content)
                .noticeType(noticeType)
                .createdAt(createdAt)
                .isRead(false)
                .build();

        shopNoticeRepository.save(shopNotice);
    }


    @Override
    public FinalResponseDto<List<NotificationResponseDto>> getNotifications(long shopId) {
        ShopEntity shopEntity = shopRepository.findById(shopId);
        if (shopEntity == null) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        List<ShopNoticeEntity> notices = shopNoticeRepository.findByShopOrderByCreatedAtDesc(shopEntity);
        if (notices.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        List<NotificationResponseDto> result = notices.stream()
                .map(entity -> NotificationResponseDto.builder()
                        .id(entity.getId())
                        .title(entity.getTitle())
                        .content(entity.getContent())
                        .noticeType(entity.getNoticeType().name())
                        .isRead(entity.getIsRead())
                        .createdAt(entity.getCreatedAt())
                        .build())
                .toList();

        return FinalResponseDto.successWithData(result);
    }

}
