package com.jonasrosendo.demoparkingapi.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingLotsUtils {


    private static final double FIRST_15_MINUTES = 5.00;
    private static final double FIRST_60_MINUTES = 9.25;
    private static final double ADDITIONAL_PER_15_MINUTES = 1.75;
    private static final double DISCOUNT_PERCENT = 0.30;

    public static String generateReceipt() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String receipt = localDateTime.toString().substring(0, 19);

        return receipt
                .replace("-", "")
                .replace(":", "")
                .replace("T", "-");
    }

    public static BigDecimal calculatePrice(LocalDateTime checkIn, LocalDateTime checkout) {
        long minutes = checkIn.until(checkout, ChronoUnit.MINUTES);
        double price = 0.0;

        if (minutes <= 15) {
            price = FIRST_15_MINUTES;
        } else if (minutes <= 60) {
            price = FIRST_60_MINUTES;
        } else {
            long additionalMinutes = minutes - 60;
            double factor = Math.ceil(additionalMinutes / 15.0);
            price = FIRST_60_MINUTES + (ADDITIONAL_PER_15_MINUTES * factor);
        }

        return new BigDecimal(price).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calculateDiscount(BigDecimal price, long numberOfParking) {
        BigDecimal discount = ((numberOfParking > 0 && numberOfParking % 10 == 0))
                ? price.multiply(BigDecimal.valueOf(DISCOUNT_PERCENT))
                : BigDecimal.valueOf(0.00);
        return discount.setScale(2, RoundingMode.HALF_EVEN);
    }
}
