package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;

public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturnSuccess() {
    // given
     LocationGateway locationGateway = new LocationGateway();
    // when
     Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");
    // then
     assertEquals(location.identification, "ZWOLLE-001");
  }

    @Test
    public void testWhenResolveNonExistingLocationShouldReturnException() {
        // given
        LocationGateway locationGateway = new LocationGateway();

        // then
        assertThrows(LocationGatewayException.class, () -> {
            locationGateway.resolveByIdentifier("ZWOLLE-011");
        });
    }

    @Test
    public void testWhenResolveLocationWithNullIdentifier() {
        // given
        LocationGateway locationGateway = new LocationGateway();
        // when & then
        assertThrows(LocationGatewayException.class, () -> {
            locationGateway.resolveByIdentifier(null);
        });
    }

    @Test
    public void testWhenResolveLocationWithEmptyIdentifier() {
        // given
        LocationGateway locationGateway = new LocationGateway();
        // when & then
        assertThrows(LocationGatewayException.class, () -> {
            locationGateway.resolveByIdentifier("");
        });
    }
}
