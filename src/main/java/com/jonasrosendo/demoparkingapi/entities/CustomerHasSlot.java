package com.jonasrosendo.demoparkingapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "customers_has_slots")
@EntityListeners(AuditingEntityListener.class)
public class CustomerHasSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receipt", nullable = false, unique = true, length = 15)
    private String receipt;

    @Column(name = "car_plate", nullable = false, length = 8)
    private String carPlate;

    @Column(name = "car_brand", nullable = false, length = 45)
    private String carBrand;

    @Column(name = "car_model", nullable = false, length = 45)
    private String carModel;

    @Column(name = "car_color", nullable = false, length = 45)
    private String carColor;

    @Column(name = "check_in", nullable = false)
    private LocalDateTime checkIn;

    @Column(name = "checkout")
    private LocalDateTime checkout;

    @Column(name = "price", columnDefinition = "decimal(7,2)")
    private BigDecimal price;

    @Column(name = "discount", columnDefinition = "decimal(7,2)")
    private BigDecimal discount;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "parking_slot_id", nullable = false)
    private ParkingSlot parkingSlot;

    @Column(name = "creation_date")
    @CreatedDate
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    @LastModifiedDate
    private LocalDateTime modificationDate;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "modified_by")
    @LastModifiedBy
    private String modifiedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerHasSlot that = (CustomerHasSlot) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
