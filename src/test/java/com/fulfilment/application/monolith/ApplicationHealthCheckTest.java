package com.fulfilment.application.monolith;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@QuarkusTest
class ApplicationHealthCheckTest {
    @Test
    public void testLivenessEndpoint() {
        given()
                .when()
                .get("/health/live")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo("UP"))
                // Check that our specific health check is included in the 'checks' array
                .body("checks.name", hasItems("Application is running"))
                .body("checks.status", hasItems("UP"));
    }
}
