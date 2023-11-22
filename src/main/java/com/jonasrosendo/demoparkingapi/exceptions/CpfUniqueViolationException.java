package com.jonasrosendo.demoparkingapi.exceptions;

public class CpfUniqueViolationException extends RuntimeException {
    public CpfUniqueViolationException(String message) {
        super(message);
    }
}
