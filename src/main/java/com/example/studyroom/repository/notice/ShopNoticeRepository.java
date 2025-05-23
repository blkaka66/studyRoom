package com.example.studyroom.repository.notice;

import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.notice.MemberNoticeEntity;
import com.example.studyroom.model.notice.ShopNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopNoticeRepository extends JpaRepository<ShopNoticeEntity, Long> {
    List<ShopNoticeEntity> findByShopIdOrderByCreatedAtDesc(Long shopId);

    ShopNoticeEntity findByShopIdAndId(Long shopId, Long id);

    Long shop(ShopEntity shop);
}
