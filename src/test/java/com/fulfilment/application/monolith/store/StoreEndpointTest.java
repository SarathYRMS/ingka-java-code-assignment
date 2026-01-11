package com.fulfilment.application.monolith.store;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

@QuarkusTest
class StoreEndpointTest {

    private static final String PATH = "stores";
    @Test
    void testCrudStores() {
        // List all, should have all 3 stores the database has initially:
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(
                        containsString("HAARLEM"),
                        containsString("AMSTERDAM"),
                        containsString("HENGELO"));

        // Delete the HAARLEM:
        given().when().delete(PATH + "/1").then().statusCode(204);

        // List all, HAARLEM should be missing now:
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(
                        not(containsString("HAARLEM")),
                        containsString("AMSTERDAM"),
                        containsString("HENGELO"));
    }

    @Test
    void testCreateStores() {
        String requestBody = "{\"name\": \"VLEUTEN\", \"quantityProductsInStock\": 10}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body(
                        containsString("VLEUTEN"),
                        containsString("10"));
    }

    @Test
    public void testGetStore() {
        // create a warehouse
        given()
                .when()
                .get(PATH+"/1")
                .then()
                .statusCode(200)
                .body(
                        Matchers.containsString("HAARLEM"),
                        Matchers.containsString("10"));
    }

}
