package com.jonasrosendo.demoparkingapi.web.vos.parking_lot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CustomerHasSlotResponseVO {

    @JsonProperty(value = "car_plate")
    private String carPlate;

    @JsonProperty(value = "car_brand")
    private String carBrand;

    @JsonProperty(value = "car_model")
    private String carModel;

    @JsonProperty(value = "car_color")
    private String carColor;

    @JsonProperty(value = "customer_cpf")
    private String customerCpf;

    private String receipt;

    @JsonProperty("check_in")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkIn;

    @JsonProperty("checkout")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkout;

    @JsonProperty("parking_slot_code")
    private String parkingSlotCode;

    private BigDecimal price;

    private BigDecimal discount;
}
