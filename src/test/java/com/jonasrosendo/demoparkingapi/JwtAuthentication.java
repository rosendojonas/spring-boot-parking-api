package com.jonasrosendo.demoparkingapi;

import com.jonasrosendo.demoparkingapi.jwt.JwtToken;
import com.jonasrosendo.demoparkingapi.web.dtos.user.UserLoginDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;
import java.util.function.Consumer;

public class JwtAuthentication {

    public static Consumer<HttpHeaders> getHeaderAuthorization(
            WebTestClient webTestClient,
            String username,
            String password
    ) {
        String token = Objects.requireNonNull(webTestClient
                .post()
                .uri("/api/v1/auth")
                .bodyValue(new UserLoginDTO(username, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody()).getToken();

        return httpHeaders -> httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
