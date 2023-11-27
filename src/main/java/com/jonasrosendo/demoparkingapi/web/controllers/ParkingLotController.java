package com.jonasrosendo.demoparkingapi.web.controllers;

import com.jonasrosendo.demoparkingapi.entities.CustomerHasSlot;
import com.jonasrosendo.demoparkingapi.exceptions.ErrorMessage;
import com.jonasrosendo.demoparkingapi.jwt.JwtUserDetails;
import com.jonasrosendo.demoparkingapi.repositories.projection.CustomerHasLotsProjection;
import com.jonasrosendo.demoparkingapi.services.CustomerHasSlotService;
import com.jonasrosendo.demoparkingapi.services.ParkingLotService;
import com.jonasrosendo.demoparkingapi.web.dtos.parking_lot.CustomerHasSlotCreateDTO;
import com.jonasrosendo.demoparkingapi.web.mappers.CustomerHasSlotMapper;
import com.jonasrosendo.demoparkingapi.web.mappers.PageableMapper;
import com.jonasrosendo.demoparkingapi.web.vos.PageableResponseVO;
import com.jonasrosendo.demoparkingapi.web.vos.parking_lot.CustomerHasSlotResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Parking lot", description = "All operations related to parking lot control")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;
    private final CustomerHasSlotService customerHasSlotService;

    @Operation(
            summary = "Register a customer to an available slot",
            description = "Register a customer to an available slot",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Success",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL to find the receipt")
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
                            responseCode = "404",
                            description = "Cpf not found/invalid or no slots available",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not permitted to customers register themselves to an available slot",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerHasSlotResponseVO> checkIn(@RequestBody @Valid CustomerHasSlotCreateDTO customerHasSlotCreateDTO) {

        CustomerHasSlot customerHasSlot = CustomerHasSlotMapper.toCustomerHasSlot(customerHasSlotCreateDTO);
        CustomerHasSlotResponseVO response = CustomerHasSlotMapper.toCustomerHasSlotResponseVO(parkingLotService.checkIn(customerHasSlot));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{receipt}")
                .buildAndExpand(response.getReceipt())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(
            summary = "Find a receipt",
            description = "Find a receipt",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "receipt", description = "receipt number generated by check-in")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerHasSlotResponseVO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Receipt not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @GetMapping("/check-in/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<CustomerHasSlotResponseVO> findByReceipt(@PathVariable String receipt) {
        CustomerHasSlot customerHasSlot = customerHasSlotService.findByReceipt(receipt);
        CustomerHasSlotResponseVO customerHasSlotResponseVO = CustomerHasSlotMapper.toCustomerHasSlotResponseVO(customerHasSlot);
        return ResponseEntity.ok(customerHasSlotResponseVO);
    }

    @Operation(
            summary = "Register a customer checkout",
            description = "Register a customer checkout",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Success"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "receipt not found or invalid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not permitted to customers register checkout",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @PutMapping("/checkout/{receipt}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerHasSlotResponseVO> checkout(@PathVariable String receipt) {
        CustomerHasSlot customerHasSlot = parkingLotService.checkout(receipt);
        CustomerHasSlotResponseVO customerHasSlotResponseVO = CustomerHasSlotMapper.toCustomerHasSlotResponseVO(customerHasSlot);
        return ResponseEntity.ok(customerHasSlotResponseVO);
    }

    @Operation(
            summary = "find all customer parking by cpf",
            description = "find all customer parking by cpf",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = PageableResponseVO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not permitted to customers see customers' parking",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableResponseVO> findAllParkingByCpf(
            @PathVariable String cpf,
            @PageableDefault(size = 5, sort = "checkIn", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<CustomerHasLotsProjection> projection = customerHasSlotService.findAllByCustomerCpf(cpf, pageable);
        PageableResponseVO pageableResponseVO = PageableMapper.toPageableVO(projection);
        return ResponseEntity.ok(pageableResponseVO);
    }

    @Operation(
            summary = "find all customer parking by id",
            description = "find all customer parking by id",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(implementation = PageableResponseVO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not permitted to admins see customers' parking",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PageableResponseVO> findAllParkingById(
            @AuthenticationPrincipal JwtUserDetails jwtUserDetails,
            @PageableDefault(size = 5, sort = "checkIn", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<CustomerHasLotsProjection> projection = customerHasSlotService.findAllByUserId(jwtUserDetails.getId(), pageable);
        PageableResponseVO pageableResponseVO = PageableMapper.toPageableVO(projection);
        return ResponseEntity.ok(pageableResponseVO);
    }
}
