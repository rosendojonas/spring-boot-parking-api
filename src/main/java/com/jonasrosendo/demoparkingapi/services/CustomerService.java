package com.jonasrosendo.demoparkingapi.services;

import com.jonasrosendo.demoparkingapi.entities.Customer;
import com.jonasrosendo.demoparkingapi.exceptions.CpfUniqueViolationException;
import com.jonasrosendo.demoparkingapi.exceptions.EntityNotFoundException;
import com.jonasrosendo.demoparkingapi.repositories.CustomerRepository;
import com.jonasrosendo.demoparkingapi.repositories.projection.CustomerProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer create(Customer customer) {
        try {
            return customerRepository.save(customer);
        } catch (DataIntegrityViolationException e) {
            throw new CpfUniqueViolationException(
                    String.format("CPF '%s' already registered.", customer.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Customer id=%s not found", id))
        );
    }

    @Transactional(readOnly = true)
    public Page<CustomerProjection> findAll(Pageable pageable) {
        return customerRepository.findAllCustomers(pageable);
    }

    @Transactional(readOnly = true)
    public Customer findByUserId(Long id) {
        return customerRepository.findByUserId(id);
    }

    @Transactional(readOnly = true)
    public Customer findByCpf(String cpf) {
        return customerRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException(String.format("Customer cpf=%s not found", cpf))
        );
    }
}
