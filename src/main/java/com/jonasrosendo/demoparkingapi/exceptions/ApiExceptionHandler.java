package com.jonasrosendo.demoparkingapi.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request,
            BindingResult result
    ) {

        HttpStatus statusCode = HttpStatus.UNPROCESSABLE_ENTITY;

        log.error("Api Error - ", e);

        return ResponseEntity
                .status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, statusCode, "Invalid field(s)", result));
    }

    @ExceptionHandler(
            {
                    UsernameUniqueViolationException.class,
                    CpfUniqueViolationException.class,
                    ParkingLotCodeUniqueViolationException.class
            }
    )
    public ResponseEntity<ErrorMessage> handleUniqueViolationException(
            RuntimeException e,
            HttpServletRequest request
    ) {
        HttpStatus statusCode = HttpStatus.CONFLICT;

        log.error("Api Error - ", e);

        return ResponseEntity
                .status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, statusCode, e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(
            EntityNotFoundException e,
            HttpServletRequest request
    ) {
        HttpStatus statusCode = HttpStatus.NOT_FOUND;

        log.error("Api Error - ", e);

        return ResponseEntity
                .status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, statusCode, e.getMessage()));
    }


    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<ErrorMessage> handlePasswordInvalidException(
            PasswordInvalidException e,
            HttpServletRequest request

    ) {

        HttpStatus statusCode = HttpStatus.BAD_REQUEST;

        log.error("Api Error - ", e);

        return ResponseEntity
                .status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, statusCode, e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> accessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("Api Error - ", ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.FORBIDDEN, ex.getMessage()));
    }

}
