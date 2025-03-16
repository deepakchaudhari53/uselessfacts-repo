package com.assignment.deep.exception

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

@QuarkusTest
class ExceptionMapperTest {

    @Test
    void handleGenericException() {
        given()
            .when().get("/invalid-endpoint")
            .then()
            .statusCode(404)
            .body("message", equalTo("Not Found"));
    }

    @Test
    void handleFactStorageException() {
        // Simulate storage failure through integration test
        given()
            .when().post("/facts")
            .then()
            .statusCode(500)
            .body("message", containsString("Failed to store fact"));
    }
}