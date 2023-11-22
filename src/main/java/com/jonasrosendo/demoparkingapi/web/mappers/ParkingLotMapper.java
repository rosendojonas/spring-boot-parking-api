package com.jonasrosendo.demoparkingapi.web.mappers;

import com.jonasrosendo.demoparkingapi.entities.ParkingSlot;
import com.jonasrosendo.demoparkingapi.web.dtos.parking_lot.ParkingSlotCreateDTO;
import com.jonasrosendo.demoparkingapi.web.vos.parking_lot.ParkingSlotResponseVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingLotMapper {

    public static ParkingSlot toParkingSlot(ParkingSlotCreateDTO parkingSlotCreateDTO) {
        return new ModelMapper().map(parkingSlotCreateDTO, ParkingSlot.class);
    }

    public static ParkingSlotResponseVO toParkingSlotResponseVO(ParkingSlot parkingSlot) {
        return new ModelMapper().map(parkingSlot, ParkingSlotResponseVO.class);
    }
}
