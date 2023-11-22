package com.jonasrosendo.demoparkingapi.web.controllers;

import com.jonasrosendo.demoparkingapi.entities.ParkingSlot;
import com.jonasrosendo.demoparkingapi.exceptions.ErrorMessage;
import com.jonasrosendo.demoparkingapi.services.ParkingSlotService;
import com.jonasrosendo.demoparkingapi.web.dtos.parking_lot.ParkingSlotCreateDTO;
import com.jonasrosendo.demoparkingapi.web.mappers.ParkingLotMapper;
import com.jonasrosendo.demoparkingapi.web.vos.parking_lot.ParkingSlotResponseVO;
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

@Tag(name = "Parking slots", description = "All operations related to parking slots")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-slots")
public class ParkingSlotController {

    private final ParkingSlotService parkingSlotService;

    @Operation(
            summary = "Create a new parking slot",
            description = "Operation to create a new parking slot",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Success",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL to find the created parking slot")
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Parking slot already registered",
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
                            description = "Not permitted to customers create new parking slots",
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
    public ResponseEntity<Void> create(@RequestBody @Valid ParkingSlotCreateDTO parkingSlotCreateDTO) {
        ParkingSlot parkingLot = ParkingLotMapper.toParkingSlot(parkingSlotCreateDTO);
        parkingSlotService.save(parkingLot);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{code}")
                .buildAndExpand(parkingLot.getCode()).toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(
            summary = "Find slot by code",
            description = "Operation to get a lot from database through code",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ParkingSlotResponseVO.class)
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
                            description = "Slot not found",
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
    public ResponseEntity<ParkingSlotResponseVO> getByCode(@PathVariable String code) {
        ParkingSlot parkingLot = parkingSlotService.findByCode(code);
        return ResponseEntity.ok(ParkingLotMapper.toParkingSlotResponseVO(parkingLot));
    }
}
