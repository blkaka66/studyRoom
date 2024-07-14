package com.example.studyroom.service;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl extends BaseServiceImpl<ShopEntity> implements ShopService {
    private final ShopRepository repository;
    private final MemberService memberService;

    public ShopServiceImpl(ShopRepository repository, MemberService memberService) {
        super(repository);
        this.repository = repository;
        this.memberService = memberService;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public List<MemberEntity> getMemberList(Long shopId) {
        Optional<ShopEntity> shopOptional = this.repository.findById(shopId);
        if(shopOptional.isEmpty()) {
            return null;
        }

        List<MemberEntity> memberList = memberService.findByShop(shopOptional.get());
        return memberList.stream()
                .filter(x -> !Objects.equals(x.getName(), "이형수"))
                .collect(Collectors.toList());
    }
}
