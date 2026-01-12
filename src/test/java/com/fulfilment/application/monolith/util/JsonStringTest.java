package com.fulfilment.application.monolith.util;

import com.warehouse.api.beans.Warehouse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonStringTest {

    @Test
    void testGetValueAsString_Success() {
        // Arrange
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU123");
        warehouse.setCapacity(100);
        // Act
        String jsonString = JsonString.getValueAsString(warehouse);
        // Assert
        assertEquals("{\"businessUnitCode\":\"BU123\",\"capacity\":100}", jsonString);
    }

    @Test
    void testGetValueAsString_ThrowsException() {
        // Arrange
        Object invalidObject = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Invalid object");
            }
        };

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> JsonString.getValueAsString(invalidObject));
        assertFalse(exception.getMessage().contains("Invalid object"));
    }
}