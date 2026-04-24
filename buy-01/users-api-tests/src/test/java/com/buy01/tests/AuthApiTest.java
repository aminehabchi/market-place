package com.buy01.tests;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.*;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthApiTest extends BaseSpec {

    private static final String REGISTER_URL = "/api/users/register";
    private static final String LOGIN_URL    = "/api/users/login";

    // unique email per run to avoid duplicate conflicts
    private static final String TEST_EMAIL =
            "testuser_" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    private static final String TEST_PASSWORD = "password123";

    // ─── REGISTER ────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Register - happy path returns 200 and msg field")
    void register_happyPath() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "email": "%s",
                  "name": "Test User",
                  "password": "%s",
                  "role": "CLIENT"
                }
                """.formatted(TEST_EMAIL, TEST_PASSWORD))
        .when()
            .post(REGISTER_URL)
        .then()
            .spec(responseSpec200)
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("register-response-schema.json"))
            .body("msg", notNullValue())
            .body("msg", not(emptyString()));
    }

    @Test
    @Order(2)
    @DisplayName("Register - duplicate email returns 4xx")
    void register_duplicateEmail() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "email": "%s",
                  "name": "Test User",
                  "password": "%s",
                  "role": "CLIENT"
                }
                """.formatted(TEST_EMAIL, TEST_PASSWORD))
        .when()
            .post(REGISTER_URL)
        .then()
            .statusCode(greaterThanOrEqualTo(400));
    }

    @Test
    @Order(3)
    @DisplayName("Register - missing email returns 400")
    void register_missingEmail() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "name": "Test User",
                  "password": "password123",
                  "role": "CLIENT"
                }
                """)
        .when()
            .post(REGISTER_URL)
        .then()
            .spec(responseSpec400);
    }

    @Test
    @Order(4)
    @DisplayName("Register - invalid email format returns 400")
    void register_invalidEmail() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "email": "not-an-email",
                  "name": "Test User",
                  "password": "password123",
                  "role": "CLIENT"
                }
                """)
        .when()
            .post(REGISTER_URL)
        .then()
            .spec(responseSpec400);
    }

    @Test
    @Order(5)
    @DisplayName("Register - password too short returns 400")
    void register_passwordTooShort() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "email": "short@test.com",
                  "name": "Test User",
                  "password": "123",
                  "role": "CLIENT"
                }
                """)
        .when()
            .post(REGISTER_URL)
        .then()
            .spec(responseSpec400);
    }

    @Test
    @Order(6)
    @DisplayName("Register - invalid role returns 400")
    void register_invalidRole() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "email": "role@test.com",
                  "name": "Test User",
                  "password": "password123",
                  "role": "SUPERADMIN"
                }
                """)
        .when()
            .post(REGISTER_URL)
        .then()
            .spec(responseSpec400);
    }

    @Test
    @Order(7)
    @DisplayName("Register - name too short returns 400")
    void register_nameTooShort() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "email": "name@test.com",
                  "name": "A",
                  "password": "password123",
                  "role": "CLIENT"
                }
                """)
        .when()
            .post(REGISTER_URL)
        .then()
            .spec(responseSpec400);
    }

    // ─── LOGIN ────────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("Login - happy path returns token, role, message")
    void login_happyPath() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "identification": "%s",
                  "password": "%s"
                }
                """.formatted(TEST_EMAIL, TEST_PASSWORD))
        .when()
            .post(LOGIN_URL)
        .then()
            .spec(responseSpec200)
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("login-response-schema.json"))
            .body("token", notNullValue())
            .body("token", not(emptyString()))
            .body("role", notNullValue())
            .body("message", notNullValue());
    }

    @Test
    @Order(9)
    @DisplayName("Login - wrong password returns 401")
    void login_wrongPassword() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "identification": "%s",
                  "password": "wrongpassword"
                }
                """.formatted(TEST_EMAIL))
        .when()
            .post(LOGIN_URL)
        .then()
            .spec(responseSpec401);
    }

    @Test
    @Order(10)
    @DisplayName("Login - non-existent user returns 401")
    void login_nonExistentUser() {
        given()
            .spec(requestSpec)
            .body("""
                {
                  "identification": "ghost@nowhere.com",
                  "password": "password123"
                }
                """)
        .when()
            .post(LOGIN_URL)
        .then()
            .spec(responseSpec401);
    }

    @Test
    @Order(11)
    @DisplayName("Login - empty body returns 4xx")
    void login_emptyBody() {
        given()
            .spec(requestSpec)
            .body("{}")
        .when()
            .post(LOGIN_URL)
        .then()
            .statusCode(greaterThanOrEqualTo(400));
    }
}
