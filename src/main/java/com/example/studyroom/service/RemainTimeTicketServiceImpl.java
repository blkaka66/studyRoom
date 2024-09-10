package com.example.studyroom.service;

import com.example.studyroom.model.RemainTimeTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class RemainTimeTicketServiceImpl extends BaseServiceImpl<RemainTimeTicketEntity> implements RemainTimeTicketService {

    public RemainTimeTicketServiceImpl(JpaRepository<RemainTimeTicketEntity, Long> repository) {
        super(repository);
    }
}
