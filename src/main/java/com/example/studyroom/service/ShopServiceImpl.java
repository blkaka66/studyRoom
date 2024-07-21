package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.ShopListResponseDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.ShopRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        Optional<ShopEntity> shop = this.findById(shopId);
        if(shop.isPresent()) {
            return this.memberService.findByShop(shop.get());
        }
        else {
            return new ArrayList<>();
        }
    }

    @Override //지점목록 가져오기
    public List<ShopEntity> getShopList() {//shopId가 안들어오면 모든 리스트를 보내고 shopid가 들어오면 해당 shop리스트만 보내고
        return this.findAll();
    }

    @Override
    //위에서 받은 리스트를 ShopListResponseDto로바꾸고싶음
    public List<ShopListResponseDto> getShopListResponseDto(Long shopId) {
        List<MemberEntity> members = getMemberList(shopId);
        return members.stream()
                .map(member -> ShopListResponseDto.builder()
                        .shopId(member.getShop().getId())
                        .name(member.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override //로그인
    public ShopEntity login(String email, String password) {
        //레포지토리에있는 함수가져오기
        ShopEntity Shop = repository.findByEmailAndPassword(email, password);

        if (Shop != null) {
            // 점주 존재하면 로그인 성공
            return Shop;
        } else {
            throw new RuntimeException("로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
    }


    @Override //회원가입
    public ShopEntity signUp(ShopSignUpRequestDto dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        ShopEntity shop = dto.toEntity();
        return repository.save(shop);
    }

    @Override // 지점정보가져오기
    public ShopEntity getShopInfo(Long shopId) {
        if (shopId ==null) {
            throw new RuntimeException("존재하지않는 id");
        }
        return repository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 지점입니다."));

//        return ShopListResponseDto.builder()
//                .shopId(shop.getId())
//                .name(shop.getName())
//                .build();
    }

}
