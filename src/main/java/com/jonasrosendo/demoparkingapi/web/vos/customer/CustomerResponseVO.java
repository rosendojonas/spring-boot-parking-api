package com.jonasrosendo.demoparkingapi.web.vos.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CustomerResponseVO {

    private Long id;
    private String name;
    private String cpf;
}
