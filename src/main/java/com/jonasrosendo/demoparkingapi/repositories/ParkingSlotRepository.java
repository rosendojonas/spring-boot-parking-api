package com.jonasrosendo.demoparkingapi.repositories;

import com.jonasrosendo.demoparkingapi.entities.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    Optional<ParkingSlot> findByCode(String code);

    Optional<ParkingSlot> findFirstByStatus(ParkingSlot.SlotStatus slotStatus);
}
