package com.jonasrosendo.demoparkingapi.services;

import com.jonasrosendo.demoparkingapi.entities.Customer;
import com.jonasrosendo.demoparkingapi.entities.CustomerHasSlot;
import com.jonasrosendo.demoparkingapi.entities.ParkingSlot;
import com.jonasrosendo.demoparkingapi.utils.ParkingLotsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ParkingLotService {

    private final CustomerHasSlotService customerHasSlotService;
    private final CustomerService customerService;
    private final ParkingSlotService parkingSlotService;

    @Transactional
    public CustomerHasSlot checkIn(CustomerHasSlot customerHasSlot) {
        Customer customer = customerService.findByCpf(customerHasSlot.getCustomer().getCpf());
        customerHasSlot.setCustomer(customer);

        ParkingSlot parkingSlot = parkingSlotService.findAvailableSlot();
        parkingSlot.setStatus(ParkingSlot.SlotStatus.UNAVAILABLE);

        customerHasSlot.setParkingSlot(parkingSlot);
        customerHasSlot.setCheckIn(LocalDateTime.now());

        customerHasSlot.setReceipt(ParkingLotsUtils.generateReceipt());
        return customerHasSlotService.save(customerHasSlot);
    }

    @Transactional
    public CustomerHasSlot checkout(String receipt) {
        CustomerHasSlot customerHasSlot = customerHasSlotService.findByReceipt(receipt);
        LocalDateTime checkoutTime = LocalDateTime.now();
        BigDecimal price = ParkingLotsUtils.calculatePrice(customerHasSlot.getCheckIn(), checkoutTime);
        customerHasSlot.setPrice(price);

        long numberOfParking = customerHasSlotService.findTotalNumberOfParkingComplete(customerHasSlot.getCustomer().getCpf());
        BigDecimal discount = ParkingLotsUtils.calculateDiscount(price, numberOfParking);
        customerHasSlot.setDiscount(discount);

        customerHasSlot.setCheckout(checkoutTime);
        customerHasSlot.getParkingSlot().setStatus(ParkingSlot.SlotStatus.AVAILABLE);

        return customerHasSlotService.save(customerHasSlot);
    }
}
