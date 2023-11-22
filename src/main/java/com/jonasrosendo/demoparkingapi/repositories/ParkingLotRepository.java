package com.jonasrosendo.demoparkingapi.repositories;

import com.jonasrosendo.demoparkingapi.entities.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    Optional<ParkingLot> findByCode(String code);
}
