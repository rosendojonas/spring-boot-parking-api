package com.jonasrosendo.demoparkingapi.web.mappers;

import com.jonasrosendo.demoparkingapi.entities.CustomerHasSlot;
import com.jonasrosendo.demoparkingapi.web.dtos.parking_lot.CustomerHasSlotCreateDTO;
import com.jonasrosendo.demoparkingapi.web.vos.parking_lot.CustomerHasSlotResponseVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerHasSlotMapper {

    public static CustomerHasSlot toCustomerHasSlot(CustomerHasSlotCreateDTO customerHasSlotCreateDTO) {
        return new ModelMapper().map(customerHasSlotCreateDTO, CustomerHasSlot.class);
    }

    public static CustomerHasSlotResponseVO toCustomerHasSlotResponseVO(CustomerHasSlot customerHasSlot) {
        return new ModelMapper().map(customerHasSlot, CustomerHasSlotResponseVO.class);
    }
}
