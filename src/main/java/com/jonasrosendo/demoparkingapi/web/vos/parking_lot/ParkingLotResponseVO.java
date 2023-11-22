package com.jonasrosendo.demoparkingapi.web.vos.parking_lot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotResponseVO {
    private Long id;
    private String code;
    private String status;
}
