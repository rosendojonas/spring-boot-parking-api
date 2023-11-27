package com.jonasrosendo.demoparkingapi.repositories;

import com.jonasrosendo.demoparkingapi.entities.CustomerHasSlot;
import com.jonasrosendo.demoparkingapi.repositories.projection.CustomerHasLotsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerHasRepository extends JpaRepository<CustomerHasSlot, Long> {

    Optional<CustomerHasSlot> findByReceiptAndCheckoutIsNull(String receipt);
    long countByCustomerCpfAndCheckoutIsNotNull(String cpf);

    Page<CustomerHasLotsProjection> findAllByCustomerCpf(String cpf, Pageable pageable);

    Page<CustomerHasLotsProjection> findAllByCustomerUserId(Long id, Pageable pageable);
}
