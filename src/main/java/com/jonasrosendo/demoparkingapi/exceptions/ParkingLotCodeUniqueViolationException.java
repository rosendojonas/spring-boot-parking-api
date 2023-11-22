package com.jonasrosendo.demoparkingapi.exceptions;

public class ParkingLotCodeUniqueViolationException extends RuntimeException {
    public ParkingLotCodeUniqueViolationException(String message) {
        super(message);
    }
}
