package com.jonasrosendo.demoparkingapi.web.dtos.parking_lot;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerHasSlotCreateDTO {

    @JsonAlias(value = "car_plate")
    @NotBlank
    @Size(min = 8, max = 8)
    @Pattern(regexp = "[A-Z]{3}-[0-9]{4}", message = "Car license plate should follow pattern 'XXX-0000'")
    private String carPlate;

    @JsonProperty(value = "car_brand")
    @NotBlank
    private String carBrand;

    @JsonProperty(value = "car_model")
    @NotBlank
    private String carModel;

    @JsonProperty(value = "car_color")
    @NotBlank
    private String carColor;

    @NotBlank
    @Size(min = 11, max = 11)
    @CPF
    @JsonProperty(value = "customer_cpf")
    private String customerCpf;
}
