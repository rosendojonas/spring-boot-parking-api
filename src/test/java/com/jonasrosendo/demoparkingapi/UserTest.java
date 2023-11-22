package com.jonasrosendo.demoparkingapi;

import com.jonasrosendo.demoparkingapi.exceptions.ErrorMessage;
import com.jonasrosendo.demoparkingapi.web.dtos.user.UserCreateDTO;
import com.jonasrosendo.demoparkingapi.web.dtos.user.UserPasswordDTO;
import com.jonasrosendo.demoparkingapi.web.vos.user.UserResponseVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserTest {

    @Autowired
    WebTestClient webTestClient;

    private static final String USER_PASSWORD = "123456";
    private static final String CUSTOMER_PASSWORD_ENCRYPTED = "$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy";

    @Test
    public void createUser_usernameAndPasswordValid_ReturnCreatedUserWith201HttpStatus() {
        UserResponseVO responseBody = createUser("tody@email.com", "123456");

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getUsername()).isEqualTo("tody@email.com");
        assertThat(responseBody.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    public void createUser_usernameInvalid_ReturnErrorMessage422HttpStatus() {
        String password = "123456";
        int expectedStatus = 422;

        ErrorMessage responseBody = createError("", password, expectedStatus);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(expectedStatus);

        responseBody = createError("todyemail.com", password, expectedStatus);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(expectedStatus);

        responseBody = createError("tody@email", password, expectedStatus);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(expectedStatus);

        responseBody = createError("tody@email.", password, expectedStatus);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(expectedStatus);

        responseBody = createError("tody@email.c", password, expectedStatus);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUser_passwordInvalid_ReturnErrorMessage422HttpStatus() {
        String username = "tody@email.com";
        int expectedStatus = 422;

        ErrorMessage responseBody = createError(username, "", expectedStatus);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(expectedStatus);

        responseBody = createError(username, "12345", expectedStatus);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(expectedStatus);

        responseBody = createError(username, "1234567", expectedStatus);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(expectedStatus);
    }

    @Test
    public void createUser_repeatedUsername_ReturnErrorMessage409HttpStatus() {

        ErrorMessage responseBody = createError("ana@email.com", "123456", 409);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);

    }

    @Test
    public void findUser_idExistent_ReturnUserWith200HttpStatus() {
        String adminUsername = "jonas@email.com";
        String customerUsername = "ana@email.com";

        // admin searching admin type
        UserResponseVO responseBody = findUser(100L, adminUsername);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(100L);
        assertThat(responseBody.getUsername()).isEqualTo("jonas@email.com");
        assertThat(responseBody.getRole()).isEqualTo("ADMIN");

        // admin searching customer type
        responseBody = findUser(101L, adminUsername);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(101L);
        assertThat(responseBody.getUsername()).isEqualTo("ana@email.com");
        assertThat(responseBody.getRole()).isEqualTo("CUSTOMER");

        // customer searching his own data
        responseBody = findUser(101L, customerUsername);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(101L);
        assertThat(responseBody.getUsername()).isEqualTo("ana@email.com");
        assertThat(responseBody.getRole()).isEqualTo("CUSTOMER");

    }

    @Test
    public void findUser_idNonexistent_ReturnUserWith404HttpStatus() {
        String adminUsername = "jonas@email.com";
        String customerUsername = "ana@email.com";

        ErrorMessage responseBody = findNonexistentOrForbiddenUser(0L, adminUsername, 404);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);

        responseBody = findNonexistentOrForbiddenUser(102L, customerUsername, 403);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void updatePassword_dataValid_Return204HttpStatus() {
        String adminUsername = "jonas@email.com";
        String customerUsername = "ana@email.com";

        updatePassword(
                100L,
                "123456",
                "101010",
                "101010",
                adminUsername
        );

        updatePassword(
                101L,
                "123456",
                "101010",
                "101010",
                customerUsername
        );
    }

    @Test
    public void updatePassword_dataInvalid_ReturnErrorBody() {
        String adminUsername = "jonas@email.com";
        String customerUsername = "ana@email.com";

        // try to update another user's password
        ErrorMessage error = updatePasswordErrors(
                101L,
                "123456",
                "101010",
                "101010",
                403,
                adminUsername
        );

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(403);

        // try to update another user's password
        error = updatePasswordErrors(
                102L,
                "123456",
                "101010",
                "101010",
                403,
                customerUsername
        );

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(403);

        error = updatePasswordErrors(
                100L,
                "123457",
                "101010",
                "101010",
                400,
                adminUsername
        );

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(400);

        error = updatePasswordErrors(
                100L,
                "123456",
                "101015",
                "101010",
                400,
                adminUsername
        );

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(400);

        error = updatePasswordErrors(
                100L,
                "123456",
                "101010",
                "101015",
                400,
                adminUsername
        );

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(400);

        error = updatePasswordErrors(
                100L,
                "123457",
                "101015",
                "101010",
                400,
                adminUsername
        );

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(400);

        error = updatePasswordErrors(
                100L,
                "123457",
                "101010",
                "101015",
                400,
                adminUsername
        );

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(400);

        error = updatePasswordErrors(
                100L,
                "12345",
                "101010",
                "101015",
                422,
                adminUsername
        );

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(422);

        error = updatePasswordErrors(
                100L,
                "1234567",
                "101010",
                "101015",
                422,
                adminUsername
        );

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(422);

    }

    @Test
    public void findAllUsers_checkAllUsers_returnUsersListWith200HttpStatusIfUserIsAdmin() {

        List<UserResponseVO> response = findAllUsers("jonas@email.com");

        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(3);
    }

    @Test
    public void findAllUsers_checkAllUsers_returnErrorResponseIfUserIsNotAdmin() {

        ErrorMessage response = findAllUsersError("ana@email.com");

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    private ErrorMessage findNonexistentOrForbiddenUser(Long invalidId, String username, int expectedHttpStatus) {
        return webTestClient
                .get()
                .uri("/api/v1/users/" + invalidId)
                .headers(
                        JwtAuthentication.getHeaderAuthorization(webTestClient, username, USER_PASSWORD)
                )
                .exchange()
                .expectStatus().isEqualTo(expectedHttpStatus)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
    }

    private List<UserResponseVO> findAllUsers(String username) {
        return webTestClient
                .get()
                .uri("/api/v1/users")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, username, USER_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseVO.class)
                .returnResult().getResponseBody();
    }

    private ErrorMessage findAllUsersError(String username) {
        return webTestClient
                .get()
                .uri("/api/v1/users")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, username, USER_PASSWORD))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
    }

    private void updatePassword(
            Long id,
            String currentPassword,
            String newPassword,
            String confirmPassword,
            String username
    ) {
        webTestClient
                .patch()
                .uri("/api/v1/users/" + id + "/password")
                .headers(
                        JwtAuthentication.getHeaderAuthorization(webTestClient, username, USER_PASSWORD)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        new UserPasswordDTO(
                                currentPassword,
                                newPassword,
                                confirmPassword
                        )
                )
                .exchange()
                .expectStatus().isNoContent();
    }

    private ErrorMessage updatePasswordErrors(
            Long id,
            String currentPassword,
            String newPassword,
            String confirmPassword,
            int expectStatus,
            String username
    ) {
        return webTestClient
                .patch()
                .uri("/api/v1/users/" + id + "/password")
                .headers(
                        JwtAuthentication.getHeaderAuthorization(webTestClient, username, USER_PASSWORD)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        new UserPasswordDTO(
                                currentPassword,
                                newPassword,
                                confirmPassword
                        )
                )
                .exchange()
                .expectStatus().isEqualTo(expectStatus)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
    }

    private UserResponseVO findUser(Long id, String username) {
        return webTestClient
                .get()
                .uri("/api/v1/users/" + id)
                .headers(
                        JwtAuthentication.getHeaderAuthorization(webTestClient, username, USER_PASSWORD)
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseVO.class)
                .returnResult().getResponseBody();
    }

    private UserResponseVO createUser(String username, String password) {
        return webTestClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        new UserCreateDTO(username, password)
                )
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseVO.class)
                .returnResult().getResponseBody();
    }

    private ErrorMessage createError(String username, String password, int expectStatus) {
        return webTestClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        new UserCreateDTO(username, password)
                )
                .exchange()
                .expectStatus().isEqualTo(expectStatus)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
    }
}
