package com.jonasrosendo.demoparkingapi.web.mappers;

import com.jonasrosendo.demoparkingapi.entities.Customer;
import com.jonasrosendo.demoparkingapi.web.dtos.customer.CustomerCreateDTO;
import com.jonasrosendo.demoparkingapi.web.vos.customer.CustomerResponseVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerMapper {

    public static Customer toCustomer(CustomerCreateDTO customerCreateDTO) {
        return new ModelMapper().map(customerCreateDTO, Customer.class);
    }

    public static CustomerResponseVO toCustomerResponseVO(Customer customer) {
        return new ModelMapper().map(customer, CustomerResponseVO.class);
    }
}
