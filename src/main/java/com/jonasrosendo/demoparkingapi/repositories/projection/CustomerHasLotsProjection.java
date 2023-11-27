package com.jonasrosendo.demoparkingapi.repositories.projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public interface CustomerHasLotsProjection {

    @JsonProperty(value = "car_plate")
    String getCarPlate();

    @JsonProperty(value = "car_brand")
    String getCarBrand();

    @JsonProperty(value = "car_model")
    String getCarModel();

    @JsonProperty(value = "car_color")
    String getCarColor();

    @JsonProperty(value = "customer_cpf")
    String getCustomerCpf();

    String getReceipt();

    @JsonProperty("check_in")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getCheckIn();

    @JsonProperty("checkout")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getCheckout();

    @JsonProperty("parking_slot_code")
    String getParkingSlotCode();

    BigDecimal getPrice();

    BigDecimal getDiscount();
}
