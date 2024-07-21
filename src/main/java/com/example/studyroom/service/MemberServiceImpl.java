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

    @Override
    public MemberEntity login(String username, String password) {
        //레포지토리에있는 함수가져오기
        MemberEntity member = repository.findBynameAndpassword(username, password);

        if (member != null) {
            // 회원이 존재하면 로그인 성공
            return member;
        } else {

            throw new RuntimeException("로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}
