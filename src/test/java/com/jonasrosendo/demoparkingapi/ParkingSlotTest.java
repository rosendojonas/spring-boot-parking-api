package com.jonasrosendo.demoparkingapi;

import com.jonasrosendo.demoparkingapi.entities.ParkingSlot;
import com.jonasrosendo.demoparkingapi.web.dtos.parking_lot.ParkingSlotCreateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parking-slots/parking-slots-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parking-slots/parking-slots-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingSlotTest {

    @Autowired
    private WebTestClient webTestClient;


    @Test
    public void createSlots_WithValidData_ReturnLocation201HttpStatus() {
        webTestClient
                .post()
                .uri("/api/v1/parking-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com","123456"))
                .bodyValue(new ParkingSlotCreateDTO("A-05", ParkingSlot.SlotStatus.AVAILABLE.name()))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .exists(HttpHeaders.LOCATION);
    }

    @Test
    public void createSlots_WithCodeAlreadyExistent_ReturnError409HttpStatus() {
        webTestClient
                .post()
                .uri("/api/v1/parking-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com","123456"))
                .bodyValue(new ParkingSlotCreateDTO("A-01", ParkingSlot.SlotStatus.AVAILABLE.name()))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("status").isEqualTo(409)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-slots");
    }

    @Test
    public void createSlots_WithInvalidData_ReturnError422HttpStatus() {
        webTestClient
                .post()
                .uri("/api/v1/parking-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com","123456"))
                .bodyValue(new ParkingSlotCreateDTO("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-slots");

        webTestClient
                .post()
                .uri("/api/v1/parking-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com","123456"))
                .bodyValue(new ParkingSlotCreateDTO("A-012", "INVALID"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-slots");
    }

    @Test
    public void createSlots_WithCustomerTryingToCreateLot_ReturnError403HttpStatus() {
        webTestClient
                .post()
                .uri("/api/v1/parking-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com","123456"))
                .bodyValue(new ParkingSlotCreateDTO("A-05", ParkingSlot.SlotStatus.AVAILABLE.name()))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-slots");
    }

    @Test
    public void findSlots_WithExistentCode_ReturnParkingLot200HttpStatus() {
        webTestClient
                .get()
                .uri("/api/v1/parking-slots/A-01")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com","123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(10)
                .jsonPath("code").isEqualTo("A-01")
                .jsonPath("status").isEqualTo("AVAILABLE");
    }

    @Test
    public void findSlots_WithNonExistentCode_ReturnError404HttpStatus() {
        webTestClient
                .get()
                .uri("/api/v1/parking-slots/A-05")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com","123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("path").isEqualTo("/api/v1/parking-slots/A-05");
    }

    @Test
    public void findSlots_WithCustomerTryingToAccessResource_ReturnError403HttpStatus() {
        webTestClient
                .get()
                .uri("/api/v1/parking-slots/A-01")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com","123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("path").isEqualTo("/api/v1/parking-slots/A-01");
    }
}
