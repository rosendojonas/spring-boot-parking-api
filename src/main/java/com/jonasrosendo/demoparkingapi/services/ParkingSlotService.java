package com.jonasrosendo.demoparkingapi.services;

import com.jonasrosendo.demoparkingapi.entities.ParkingSlot;
import com.jonasrosendo.demoparkingapi.exceptions.EntityNotFoundException;
import com.jonasrosendo.demoparkingapi.exceptions.ParkingLotCodeUniqueViolationException;
import com.jonasrosendo.demoparkingapi.repositories.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingSlotService {

    private final ParkingSlotRepository slotRepository;

    @Transactional
    public ParkingSlot save(ParkingSlot parkingSlot) {
        try {
            return slotRepository.save(parkingSlot);
        } catch (DataIntegrityViolationException e) {
            throw new ParkingLotCodeUniqueViolationException(String.format("Lot code=%s already registered", parkingSlot.getCode()));
        }
    }

    @Transactional(readOnly = true)
    public ParkingSlot findByCode(String code) {
        return slotRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Lot code=%s not found", code))
        );
    }

    @Transactional(readOnly = true)
    public ParkingSlot findAvailableSlot() {
        return slotRepository.findFirstByStatus(ParkingSlot.SlotStatus.AVAILABLE).orElseThrow(
                () -> new EntityNotFoundException("No Available slots")
        );
    }
}
