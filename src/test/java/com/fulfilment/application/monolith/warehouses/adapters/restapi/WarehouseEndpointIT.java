package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import com.fulfilment.application.monolith.util.JsonString;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import com.warehouse.api.beans.Warehouse;

@QuarkusIntegrationTest
class WarehouseEndpointIT {

    private static final String PATH = "warehouse";
    @Test
    void testSimpleListWarehouses() {
        // List all, should have all 3 products the database has initially:
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(
                        containsString("MWH.001"),
                        containsString("MWH.012"),
                        containsString("MWH.023"),
                        containsString("ZWOLLE-001"),
                        containsString("AMSTERDAM-001"),
                        containsString("TILBURG-001"));

        // Archive the ZWOLLE-001:
        given().when().delete(PATH + "/1").then().statusCode(204);

        // List all, ZWOLLE-001 should be missing now:
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(
                        not(containsString("ZWOLLE-001")),
                        containsString("AMSTERDAM-001"),
                        containsString("TILBURG-001"));
    }

    @Test
    public void testCreateWarehouses() {
        String requestBody = getWarehouse("ZWOLLE-002");
        // create a warehouse
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PATH)
                .then()
                .statusCode(200)
                .body(
                        containsString("BU-001"),
                        containsString("ZWOLLE-002"),
                        containsString("50"),
                        containsString("10"));
    }


    @Test
    public void testReplaceWarehouses() {
        String requestBody = getWarehouse("ZWOLLE-002");
        // create a warehouse
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PATH)
                .then()
                .statusCode(200);
        // replace the created warehouse
        var replaceWarehouseRequest = getWarehouse("EINDHOVEN-001");

        given()
                .contentType(ContentType.JSON)
                .body(replaceWarehouseRequest)
                .when()
                .put(PATH)
                .then()
                .statusCode(200)
                .body(
                        containsString("BU-001"),
                        containsString("EINDHOVEN-001"),
                        containsString("50"),
                        containsString("10"));
    }


    private static String getWarehouse(String location) {
        var warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU-001");
        warehouse.setLocation(location);
        warehouse.setCapacity(50);
        warehouse.setStock(10);
        return JsonString.getValueAsString(warehouse);
    }

    @Test
    public void testGetWarehouses() {
        // create a warehouse
        given()
                .when()
                .get(PATH+"/1")
                .then()
                .statusCode(200)
                .body(
                        containsString("MWH.001"),
                        containsString("ZWOLLE-001"),
                        containsString("100"),
                        containsString("10"));
    }
}
