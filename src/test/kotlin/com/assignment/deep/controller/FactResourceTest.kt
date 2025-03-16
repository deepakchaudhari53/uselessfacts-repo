package com.assignment.deep.controller

import com.assignment.deep.model.ApiFactResponse
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.Header;
import org.junit.jupiter.api.Test;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class FactResourceTest {

    @InjectMock
    FactService factService;

    @InjectMock
    UselessFactsClient factsClient;

    final String validId = UUID.randomUUID().toString();
    final String invalidId = "invalid-id";
    final String apiKey = "secret_admin_key";

    @Test
    void fetchFact_Success() {
        ApiFactResponse mockResponse = new ApiFactResponse(
            "123", "Test fact", "test", "http://source.com", "en", "http://permalink.com"
        );

        when(factsClient.getRandomFact()).thenReturn(mockResponse);
        when(factService.saveFact(any())).thenReturn(true);

        given()
            .when().post("/facts")
            .then()
            .statusCode(200)
            .body("text", equalTo("Test fact"));
    }

    @Test
    void fetchFact_ExternalApiFailure() {
        when(factsClient.getRandomFact()).thenThrow(new RuntimeException("API down"));

        given()
            .when().post("/facts")
            .then()
            .statusCode(503)
            .body("message", equalTo("External service unavailable"));
    }

    @Test
    void getFact_Success() {
        CachedFact mockFact = new CachedFact(validId, "123", "Test fact",
            "test", "http://source.com", "en", "http://permalink.com", 0);

        when(factService.getFact(validId)).thenReturn(mockFact);

        given()
            .when().get("/facts/" + validId)
            .then()
            .statusCode(200)
            .body("text", equalTo("Test fact"));
    }

    @Test
    void getFact_NotFound() {
        when(factService.getFact(invalidId)).thenThrow(new FactNotFoundException(invalidId));

        given()
            .when().get("/facts/" + invalidId)
            .then()
            .statusCode(404)
            .body("message", equalTo("Fact not found"));
    }

    @Test
    void getAllFacts_Success() {
        when(factService.getAllFacts()).thenReturn(Collections.emptyList());

        given()
            .when().get("/facts")
            .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }

    @Test
    void getStatistics_Unauthorized() {
        given()
            .when().get("/facts/admin/statistics")
            .then()
            .statusCode(401);
    }

    @Test
    void getStatistics_Success() {
        given()
            .header(new Header("X-API-Key", apiKey))
        .when().get("/facts/admin/statistics")
            .then()
            .statusCode(200);
    }
}