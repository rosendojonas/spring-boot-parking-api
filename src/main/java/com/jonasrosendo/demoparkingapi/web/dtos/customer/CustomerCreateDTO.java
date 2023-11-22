package com.jonasrosendo.demoparkingapi.web.dtos.customer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreateDTO {

    @NotNull
    @Size(min = 3, max = 100)
    private String name;

    @CPF
    @Size(min = 11, max = 11)
    private String cpf;

}
