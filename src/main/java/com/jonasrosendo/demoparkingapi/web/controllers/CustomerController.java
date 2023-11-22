package com.jonasrosendo.demoparkingapi.web.controllers;

import com.jonasrosendo.demoparkingapi.entities.Customer;
import com.jonasrosendo.demoparkingapi.exceptions.ErrorMessage;
import com.jonasrosendo.demoparkingapi.jwt.JwtUserDetails;
import com.jonasrosendo.demoparkingapi.repositories.projection.CustomerProjection;
import com.jonasrosendo.demoparkingapi.services.CustomerService;
import com.jonasrosendo.demoparkingapi.services.UserService;
import com.jonasrosendo.demoparkingapi.web.dtos.customer.CustomerCreateDTO;
import com.jonasrosendo.demoparkingapi.web.mappers.CustomerMapper;
import com.jonasrosendo.demoparkingapi.web.mappers.PageableMapper;
import com.jonasrosendo.demoparkingapi.web.vos.PageableResponseVO;
import com.jonasrosendo.demoparkingapi.web.vos.customer.CustomerResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customers", description = "All operations related to customers")
@RequiredArgsConstructor
@RequestMapping("api/v1/customers")
@RestController
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;

    @Operation(
            summary = "Create a new customer",
            description = "Operation to create a new customer",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerResponseVO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Cpf already registered",
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
                            description = "Not permitted to admins create customer profiles",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponseVO> create(
            @RequestBody @Valid CustomerCreateDTO customerCreateDTO,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        Customer customer = CustomerMapper.toCustomer(customerCreateDTO);
        customer.setUser(userService.findById(userDetails.getId()));
        Customer customerResponse = customerService.create(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerMapper.toCustomerResponseVO(customerResponse));
    }

    @Operation(
            summary = "Find customer by id",
            description = "Operation to get a customer from database through id",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerResponseVO.class)
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
                            description = "Customer not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseVO> findById(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        return ResponseEntity.ok(CustomerMapper.toCustomerResponseVO(customer));
    }

    @Operation(
            summary = "Find all customers",
            description = "Operation to get all customers from database",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Page number"
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                            description = "Max number of elements per page"
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "sort",
                            hidden = true,
                            content = @Content(schema = @Schema(type = "string", defaultValue = "id,asc")),
                            description = "Sort elements"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableResponseVO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Customer has not permission to access this resource",
                            content = @Content(
                                    mediaType = "application/json;charset=UTF-",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableResponseVO> findAll(
            @Parameter(hidden = true)
            @PageableDefault(size = 5, sort = {"name"})
            Pageable pageable
    ) {
        Page<CustomerProjection> customerPage = customerService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toPageableVO(customerPage));
    }

    @Operation(
            summary = "Find customer details",
            description = "Operation to get customer details from database through customer's own id",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerResponseVO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Amin has not permission to access this resource",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
            }
    )
    @GetMapping("/details")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponseVO> findDetails(
           @AuthenticationPrincipal JwtUserDetails jwtUserDetails
    ) {

        Customer customer = customerService.findByUserId(jwtUserDetails.getId());
        return ResponseEntity.ok(CustomerMapper.toCustomerResponseVO(customer));
    }
}
