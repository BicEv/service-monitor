package ru.bicev;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class MonitoredServiceResourceTest {

    @Test
    void testCreateService_Success() {
        String validJson = """
                {
                    "name": "Google",
                    "url": "https://www.google.com",
                    "active": true,
                    "expectedStatusCode": 200,
                    "checkIntervalSeconds": 30
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(validJson)
                .when()
                .post("/api/v1/services")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", is("Google"))
                .body("url", is("https://www.google.com"));
    }

    @Test
    void testCreateService_BadRequest() {
        String invalidJson = """
                {
                    "name": "    ",
                    "url": "definetly not url",
                    "active": true,
                    "expectedStatusCode": 69,
                    "checkIntervalSeconds": 0
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post("/api/v1/services")
                .then()
                .statusCode(400);

    }

}
