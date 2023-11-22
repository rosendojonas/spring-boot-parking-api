package com.jonasrosendo.demoparkingapi.web.controllers;

import com.jonasrosendo.demoparkingapi.exceptions.ErrorMessage;
import com.jonasrosendo.demoparkingapi.jwt.JwtToken;
import com.jonasrosendo.demoparkingapi.jwt.JwtUserDetailsService;
import com.jonasrosendo.demoparkingapi.web.dtos.user.UserLoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Resource for api authentication")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    private final JwtUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "Authentication",
            description = "Resource for api authentication",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authentication successful and return a bearer token",
                            content = @Content(
                                    mediaType = "application/son",
                                    schema = @Schema(
                                            implementation = JwtToken.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials",
                            content = @Content(
                                    mediaType = "application/son",
                                    schema = @Schema(
                                            implementation = ErrorMessage.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Invalid fields",
                            content = @Content(
                                    mediaType = "application/son",
                                    schema = @Schema(
                                            implementation = ErrorMessage.class
                                    )
                            )
                    ),
            }
    )
    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(
            @RequestBody @Valid UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) {
        log.info("Authentication process for login: {}", userLoginDTO.getUsername());

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            JwtToken jwtToken = userDetailsService.getTokenAuthenticated(userLoginDTO.getUsername());
            return ResponseEntity.ok(jwtToken);
        } catch (AuthenticationException e) {
            log.warn("Bad Credentials from username {}", userLoginDTO.getUsername());
        }

        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Invalid Credentials"));
    }
}
