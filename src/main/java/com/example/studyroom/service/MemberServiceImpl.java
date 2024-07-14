package com.example.studyroom.service;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl extends BaseServiceImpl<MemberEntity> implements MemberService {
    private final MemberRepository repository;

    public MemberServiceImpl(MemberRepository repository) {
        super(repository);
        this.repository = repository;
    }


    @Override
    public List<MemberEntity> findByShop(ShopEntity shop) {
        return repository.findByShop(shop);
    }
}
