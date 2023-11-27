package com.jonasrosendo.demoparkingapi.services;

import com.jonasrosendo.demoparkingapi.entities.CustomerHasSlot;
import com.jonasrosendo.demoparkingapi.exceptions.EntityNotFoundException;
import com.jonasrosendo.demoparkingapi.repositories.CustomerHasRepository;
import com.jonasrosendo.demoparkingapi.repositories.projection.CustomerHasLotsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomerHasSlotService {

    private final CustomerHasRepository customerHasRepository;

    @Transactional
    public CustomerHasSlot save(CustomerHasSlot customerHasSlot) {
        return customerHasRepository.save(customerHasSlot);
    }

    public CustomerHasSlot findByReceipt(String receipt) {
        return customerHasRepository.findByReceiptAndCheckoutIsNull(receipt).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Receipt '%s' not found or checkout already performed", receipt)
                )
        );
    }

    @Transactional(readOnly = true)
    public long findTotalNumberOfParkingComplete(String cpf) {
        return customerHasRepository.countByCustomerCpfAndCheckoutIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public Page<CustomerHasLotsProjection> findAllByCustomerCpf(String cpf, Pageable pageable) {
        return customerHasRepository.findAllByCustomerCpf(cpf, pageable);
    }

    @Transactional(readOnly = true)
    public Page<CustomerHasLotsProjection> findAllByUserId(Long id, Pageable pageable) {
        return customerHasRepository.findAllByCustomerUserId(id, pageable);
    }
}
