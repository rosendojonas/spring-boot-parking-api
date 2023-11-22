package com.jonasrosendo.demoparkingapi.web.vos.user;

import lombok.Data;

@Data
public class UserResponseVO {

    private Long id;
    private String username;
    private String role;
}
