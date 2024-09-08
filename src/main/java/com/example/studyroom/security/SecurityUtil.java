package com.example.studyroom.security;

import com.example.studyroom.dto.requestDto.MemberSignInRequestDto;
import com.example.studyroom.dto.requestDto.ShopSignInRequestDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class SecurityUtil {
    public static ShopEntity getShopInfo() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (ShopEntity) authentication.getPrincipal();
    }

    public static MemberEntity getMemberInfo() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (MemberEntity) authentication.getPrincipal();
    }
}
