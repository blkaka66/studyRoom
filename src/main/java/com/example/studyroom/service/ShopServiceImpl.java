package com.example.studyroom.service;

import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.ShopRepository;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl extends BaseServiceImpl<ShopEntity> implements ShopService {
    private final ShopRepository repository;

    public ShopServiceImpl(ShopRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
