package com.jonasrosendo.demoparkingapi.web.controllers;

import com.jonasrosendo.demoparkingapi.entities.User;
import com.jonasrosendo.demoparkingapi.exceptions.ErrorMessage;
import com.jonasrosendo.demoparkingapi.services.UserService;
import com.jonasrosendo.demoparkingapi.web.dtos.user.UserCreateDTO;
import com.jonasrosendo.demoparkingapi.web.dtos.user.UserPasswordDTO;
import com.jonasrosendo.demoparkingapi.web.mappers.UserMapper;
import com.jonasrosendo.demoparkingapi.web.vos.user.UserResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "All operations related to users")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Operation to create a new user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Success",
                            content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponseVO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already registered",
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
                    )
            }
    )
    @PostMapping
    public ResponseEntity<UserResponseVO> create(@RequestBody @Valid UserCreateDTO userDto) {
        User createdUser = userService.save(UserMapper.toUser(userDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toUserResponseVO(createdUser));
    }

    @Operation(
            summary = "Find user by id",
            description = "Operation to get a user from database through id",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseVO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User has not permission to access this resource",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('CUSTOMER') AND #id == authentication.principal.id)")
    public ResponseEntity<UserResponseVO> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok().body(UserMapper.toUserResponseVO(user));
    }

    @Operation(
            summary = "update password",
            description = "Operation to update password",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Success"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Invalid password format",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User has not permission to access this resource",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER') AND (#id == authentication.principal.id)")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody @Valid UserPasswordDTO userPasswordDTO) {
        userService.updatePassword(
                id, userPasswordDTO.getCurrentPassword(),
                userPasswordDTO.getNewPassword(),
                userPasswordDTO.getConfirmPassword()
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "find all user",
            description = "Operation to return all users",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserResponseVO.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User has not permission to access this resource",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseVO>> findAll() {
        List<UserResponseVO> users = UserMapper.toUserResponseVOList(userService.findAll());
        return ResponseEntity.ok(users);
    }
}
