package com.example.studyroom.service;

import com.example.studyroom.model.SeatEntity;
import com.example.studyroom.repository.SeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Transactional
    public boolean updateSeatAvailability(Long seatId) {
        Optional<SeatEntity> seatOpt = seatRepository.findById(seatId);
        if (seatOpt.isEmpty()) {
            return false;
        }

        SeatEntity seat = seatOpt.get();
        seat.setAvailable(true);
        seatRepository.save(seat);
        return true;
    }
}
