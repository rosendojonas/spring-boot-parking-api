package com.jonasrosendo.demoparkingapi.web.mappers;

import com.jonasrosendo.demoparkingapi.entities.ParkingLot;
import com.jonasrosendo.demoparkingapi.web.dtos.parking_lot.ParkingLotCreateDTO;
import com.jonasrosendo.demoparkingapi.web.vos.parking_lot.ParkingLotResponseVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingLotMapper {

    public static ParkingLot toParkingLot(ParkingLotCreateDTO parkingLotCreateDTO) {
        return new ModelMapper().map(parkingLotCreateDTO, ParkingLot.class);
    }

    public static ParkingLotResponseVO toParkingLotResponseVO(ParkingLot parkingLot) {
        return new ModelMapper().map(parkingLot, ParkingLotResponseVO.class);
    }
}
