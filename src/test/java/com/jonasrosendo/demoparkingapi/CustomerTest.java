package com.jonasrosendo.demoparkingapi;


import com.jonasrosendo.demoparkingapi.exceptions.ErrorMessage;
import com.jonasrosendo.demoparkingapi.web.dtos.customer.CustomerCreateDTO;
import com.jonasrosendo.demoparkingapi.web.vos.PageableResponseVO;
import com.jonasrosendo.demoparkingapi.web.vos.customer.CustomerResponseVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/customers/customers-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/customers/customers-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CustomerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createCustomer_WithValidData_ReturnCustomer201HttpStatus() {
        CustomerResponseVO responseBody = webTestClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "toby@email.com", "123456"))
                .bodyValue(new CustomerCreateDTO("Tobias Ferreira", "88710700030"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponseVO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getName()).isEqualTo("Tobias Ferreira");
        assertThat(responseBody.getCpf()).isEqualTo("88710700030");
    }

    @Test
    public void createCustomer_WithCpfRegisteredData_ReturnErrorMessage409HttpStatus() {
        ErrorMessage responseBody = webTestClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "toby@email.com", "123456"))
                .bodyValue(new CustomerCreateDTO("Tobias Ferreira", "89097862051"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createCustomer_WithInvalidData_ReturnErrorMessage422HttpStatus() {
        ErrorMessage responseBody = webTestClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "toby@email.com", "123456"))
                .bodyValue(new CustomerCreateDTO("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "toby@email.com", "123456"))
                .bodyValue(new CustomerCreateDTO("To", "11111111111"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "toby@email.com", "123456"))
                .bodyValue(new CustomerCreateDTO("To", "887.107.000-30"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createCustomer_WithForbiddenUserData_ReturnErrorMessage403HttpStatus() {
        ErrorMessage responseBody = webTestClient
                .post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .bodyValue(new CustomerCreateDTO("Tobias Ferreira", "89097862051"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findCustomer_WithExistentIdByAdmin_ReturnCustomer200HttpStatus() {
        CustomerResponseVO responseBody = webTestClient
                .get()
                .uri("/api/v1/customers/10")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseVO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void findCustomer_WithNonExistentIdByAdmin_ReturnError404HttpStatus() {
        ErrorMessage responseBody = webTestClient
                .get()
                .uri("/api/v1/customers/0")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void findCustomer_WithIdExistentIdByCustomer_ReturnError403HttpStatus() {
        ErrorMessage responseBody = webTestClient
                .get()
                .uri("/api/v1/customers/10")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findAllCustomer_WithPaginationByAdmin_ReturnCustomerList200HttpStatus() {
        PageableResponseVO responseBody = webTestClient
                .get()
                .uri("/api/v1/customers")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableResponseVO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(2);
        assertThat(responseBody.getNumber()).isEqualTo(0);
        assertThat(responseBody.getTotalPages()).isEqualTo(1);

        responseBody = webTestClient
                .get()
                .uri("/api/v1/customers?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableResponseVO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getNumber()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void findAllCustomer_WithPaginationByCustomer_ReturnError403HttpStatus() {
        ErrorMessage responseBody = webTestClient
                .get()
                .uri("/api/v1/customers")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);

    }

    @Test
    public void findCustomer_WithCustomerTokenData_ReturnCustomer200HttpStatus() {
        CustomerResponseVO responseBody = webTestClient
                .get()
                .uri("/api/v1/customers/details")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseVO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCpf()).isEqualTo("94140627000");
        assertThat(responseBody.getName()).isEqualTo("Ana Silva");
        assertThat(responseBody.getId()).isEqualTo(10);

    }

    @Test
    public void findCustomer_WithAdminTokenData_ReturnError403HttpStatus() {
        ErrorMessage responseBody = webTestClient
                .get()
                .uri("/api/v1/customers/details")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "jonas@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);

    }
}
