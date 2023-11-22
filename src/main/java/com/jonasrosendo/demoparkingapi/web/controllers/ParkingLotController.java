package com.jonasrosendo.demoparkingapi.web.controllers;

import com.jonasrosendo.demoparkingapi.entities.ParkingLot;
import com.jonasrosendo.demoparkingapi.exceptions.ErrorMessage;
import com.jonasrosendo.demoparkingapi.services.ParkingLotService;
import com.jonasrosendo.demoparkingapi.web.dtos.parking_lot.ParkingLotCreateDTO;
import com.jonasrosendo.demoparkingapi.web.mappers.ParkingLotMapper;
import com.jonasrosendo.demoparkingapi.web.vos.customer.CustomerResponseVO;
import com.jonasrosendo.demoparkingapi.web.vos.parking_lot.ParkingLotResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Parking lots", description = "All operations related to parking lots")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @Operation(
            summary = "Create a new parking lot",
            description = "Operation to create a new parking lot",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Success",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL to find the created parking lot")
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Parking lot already registered",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Invalid input not possible to process data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not permitted to customers create new parking lots",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@RequestBody @Valid ParkingLotCreateDTO parkingLotCreateDTO) {
        ParkingLot parkingLot = ParkingLotMapper.toParkingLot(parkingLotCreateDTO);
        parkingLotService.save(parkingLot);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{code}")
                .buildAndExpand(parkingLot.getCode()).toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(
            summary = "Find lot by code",
            description = "Operation to get a lot from database through code",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ParkingLotResponseVO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Customer has not permission to access this resource",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Lot not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingLotResponseVO> getByCode(@PathVariable String code) {
        ParkingLot parkingLot = parkingLotService.findByCode(code);
        return ResponseEntity.ok(ParkingLotMapper.toParkingLotResponseVO(parkingLot));
    }
}
