package com.jonasrosendo.demoparkingapi;

import com.jonasrosendo.demoparkingapi.web.dtos.parking_lot.CustomerHasSlotCreateDTO;
import com.jonasrosendo.demoparkingapi.web.vos.PageableResponseVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parking-lots/parking-lots-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parking-lots/parking-lots-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingLotTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createCheckIn_WithValidData_ReturnLocation201HttpStatus() {
        CustomerHasSlotCreateDTO customerHasSlotCreateDTO = CustomerHasSlotCreateDTO
                .builder()
                .carPlate("XXX-0000")
                .carBrand("FIAT")
                .carModel("PALIO")
                .carColor("SILVER")
                .customerCpf("89097862051")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/parking-lots/check-in")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerHasSlotCreateDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("car_plate").isEqualTo("XXX-0000")
                .jsonPath("car_brand").isEqualTo("FIAT")
                .jsonPath("car_model").isEqualTo("PALIO")
                .jsonPath("car_color").isEqualTo("SILVER")
                .jsonPath("customer_cpf").isEqualTo("89097862051")
                .jsonPath("check_in").exists()
                .jsonPath("receipt").exists()
                .jsonPath("parking_slot_code").exists();
    }

    @Test
    public void createCheckIn_WithCustomerRole_ReturnError403HttpStatus() {
        CustomerHasSlotCreateDTO customerHasSlotCreateDTO = CustomerHasSlotCreateDTO
                .builder()
                .carPlate("XXX-0000")
                .carBrand("FIAT")
                .carModel("PALIO")
                .carColor("SILVER")
                .customerCpf("89097862051")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/parking-lots/check-in")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerHasSlotCreateDTO)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckIn_WithInvalidData_ReturnError422HttpStatus() {
        CustomerHasSlotCreateDTO customerHasSlotCreateDTO = CustomerHasSlotCreateDTO
                .builder()
                .carPlate("XXX-0000")
                .carBrand("FIAT")
                .carModel("PALIO")
                .carColor("SILVER")
                .customerCpf("11111111111")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/parking-lots/check-in")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerHasSlotCreateDTO)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in")
                .jsonPath("method").isEqualTo("POST");

        customerHasSlotCreateDTO = CustomerHasSlotCreateDTO
                .builder()
                .carPlate("")
                .carBrand("")
                .carModel("")
                .carColor("")
                .customerCpf("")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/parking-lots/check-in")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerHasSlotCreateDTO)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckIn_WithNonExistentCpf_ReturnError422HttpStatus() {
        CustomerHasSlotCreateDTO customerHasSlotCreateDTO = CustomerHasSlotCreateDTO
                .builder()
                .carPlate("XXX-0000")
                .carBrand("FIAT")
                .carModel("PALIO")
                .carColor("SILVER")
                .customerCpf("94997236098")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/parking-lots/check-in")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerHasSlotCreateDTO)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckIn_WithNoSlotAvailable_ReturnError422HttpStatus() {
        CustomerHasSlotCreateDTO customerHasSlotCreateDTO = CustomerHasSlotCreateDTO
                .builder()
                .carPlate("XXX-0000")
                .carBrand("FIAT")
                .carModel("PALIO")
                .carColor("SILVER")
                .customerCpf("94997236098")
                .build();

        // fill available slot
        webTestClient
                .post()
                .uri("/api/v1/parking-lots/check-in")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerHasSlotCreateDTO)
                .exchange();

        // fill available slot
        webTestClient
                .post()
                .uri("/api/v1/parking-lots/check-in")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerHasSlotCreateDTO)
                .exchange();

        // test unavailable slot
        webTestClient
                .post()
                .uri("/api/v1/parking-lots/check-in")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerHasSlotCreateDTO)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void findCheckIn_WithAdminRole_ReturnData200HttpStatus() {

        webTestClient
                .get()
                .uri("/api/v1/parking-lots/check-in/{receipt}", "20231122-130122")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("car_plate").isEqualTo("FIT-2020")
                .jsonPath("car_brand").isEqualTo("FIAT")
                .jsonPath("car_model").isEqualTo("PALIO")
                .jsonPath("car_color").isEqualTo("VERDE")
                .jsonPath("customer_cpf").isEqualTo("94140627000")
                .jsonPath("check_in").isEqualTo("2023-11-22 13:02:29")
                .jsonPath("receipt").isEqualTo("20231122-130122")
                .jsonPath("parking_slot_code").isEqualTo("A-01");
    }

    @Test
    public void findCheckIn_WithCustomerRole_ReturnData200HttpStatus() {

        webTestClient
                .get()
                .uri("/api/v1/parking-lots/check-in/{receipt}", "20231122-130122")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("car_plate").isEqualTo("FIT-2020")
                .jsonPath("car_brand").isEqualTo("FIAT")
                .jsonPath("car_model").isEqualTo("PALIO")
                .jsonPath("car_color").isEqualTo("VERDE")
                .jsonPath("customer_cpf").isEqualTo("94140627000")
                .jsonPath("check_in").isEqualTo("2023-11-22 13:02:29")
                .jsonPath("receipt").isEqualTo("20231122-130122")
                .jsonPath("parking_slot_code").isEqualTo("A-01");
    }

    @Test
    public void findCheckIn_WithNonExistentReceipt_ReturnError404HttpStatus() {

        webTestClient
                .get()
                .uri("/api/v1/parking-lots/check-in/{receipt}", "00000000-000000")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404);
    }

    @Test
    public void doCheckout_WithReceiptExistent_ReturnSuccess200HttpStatus() {

        webTestClient
                .put()
                .uri("/api/v1/parking-lots/checkout/{receipt}", "20231122-130122")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("car_plate").isEqualTo("FIT-2020")
                .jsonPath("car_brand").isEqualTo("FIAT")
                .jsonPath("car_model").isEqualTo("PALIO")
                .jsonPath("car_color").isEqualTo("VERDE")
                .jsonPath("customer_cpf").isEqualTo("94140627000")
                .jsonPath("check_in").isEqualTo("2023-11-22 13:02:29")
                .jsonPath("receipt").isEqualTo("20231122-130122")
                .jsonPath("parking_slot_code").isEqualTo("A-01")
                .jsonPath("price").exists()
                .jsonPath("discount").exists()
                .jsonPath("checkout").exists();
    }

    @Test
    public void doCheckout_WithReceiptNonExistent_ReturnError404HttpStatus() {

        webTestClient
                .put()
                .uri("/api/v1/parking-lots/checkout/{receipt}", "00000000-000000")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404);
    }

    @Test
    public void doCheckout_WithRoleCustomer_ReturnError403HttpStatus() {

        webTestClient
                .put()
                .uri("/api/v1/parking-lots/checkout/{receipt}", "20231122-130122")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403);
    }

    @Test
    public void findAllParkingByCpf_WithValidCpf_ReturnSuccess200HttpStatus() {

        PageableResponseVO responseBody = webTestClient
                .get()
                .uri("/api/v1/parking-lots/cpf/{cpf}?size=1&page=0", "94140627000")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableResponseVO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(2);
        assertThat(responseBody.getNumber()).isEqualTo(0);
        assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = webTestClient
                .get()
                .uri("/api/v1/parking-lots/cpf/{cpf}?size=1&page=1", "94140627000")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableResponseVO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(2);
        assertThat(responseBody.getNumber()).isEqualTo(1);
        assertThat(responseBody.getSize()).isEqualTo(1);
    }

    @Test
    public void findAllParkingByCpf_WithRoleCustomer_ReturnError403HttpStatus() {
        webTestClient
                .get()
                .uri("/api/v1/parking-lots/cpf/{cpf}?size=1&page=1", "94140627000")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403);
    }

    @Test
    public void findAllParkingById_WithCustomerRole_ReturnSuccess200HttpStatus() {

        PageableResponseVO responseBody = webTestClient
                .get()
                .uri("/api/v1/parking-lots?size=1&page=0")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableResponseVO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(2);
        assertThat(responseBody.getNumber()).isEqualTo(0);
        assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = webTestClient
                .get()
                .uri("/api/v1/parking-lots?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableResponseVO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(2);
        assertThat(responseBody.getNumber()).isEqualTo(1);
        assertThat(responseBody.getSize()).isEqualTo(1);
    }

    @Test
    public void findAllParkingById_WithRoleAdmin_ReturnError403HttpStatus() {
        webTestClient
                .get()
                .uri("/api/v1/parking-lots?size=1&page=1", "94140627000")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403);
    }
}
