package com.jonasrosendo.demoparkingapi.services;

import com.jonasrosendo.demoparkingapi.entities.ParkingLot;
import com.jonasrosendo.demoparkingapi.exceptions.EntityNotFoundException;
import com.jonasrosendo.demoparkingapi.exceptions.ParkingLotCodeUniqueViolationException;
import com.jonasrosendo.demoparkingapi.repositories.ParkingLotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;

    @Transactional
    public ParkingLot save(ParkingLot parkingLot) {
        try {
            return parkingLotRepository.save(parkingLot);
        } catch (DataIntegrityViolationException e) {
            throw new ParkingLotCodeUniqueViolationException(String.format("Lot code=%s already registered", parkingLot.getCode()));
        }
    }

    @Transactional(readOnly = true)
    public ParkingLot findByCode(String code) {
        return parkingLotRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Lot code=%s not found", code))
        );
    }
}
