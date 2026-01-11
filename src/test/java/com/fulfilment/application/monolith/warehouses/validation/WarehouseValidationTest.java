package com.fulfilment.application.monolith.warehouses.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidation;
import com.fulfilment.application.monolith.warehouses.exception.WarehouseException;

class WarehouseValidationTest {

    private WarehouseStore warehouseStore;
    private LocationResolver locationResolver;
    private WarehouseValidation warehouseValidation;

    @BeforeEach
    void setUp() {
        warehouseStore = mock(WarehouseStore.class);
        locationResolver = mock(LocationResolver.class);
        warehouseValidation = new WarehouseValidation(warehouseStore, locationResolver);
    }

    @Test
    void givenAlreadyExistingBusinessCodeThenThrowException() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU123");
        when(warehouseStore.checkWarehouseBusinessCodeExists("BU123")).thenReturn(true);

        assertThrows(WarehouseException.class, () -> warehouseValidation.validateWarehouse(warehouse));
        verify(warehouseStore, times(1)).checkWarehouseBusinessCodeExists("BU123");
    }

    @Test
    void givenInvalidLocationThenThrowException() {
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("InvalidLocation");
        when(locationResolver.resolveByIdentifier("InvalidLocation")).thenReturn(null);

        assertThrows(WarehouseException.class, () -> warehouseValidation.isLocationAndWarehouseFeasible(warehouse));
        verify(locationResolver, times(1)).resolveByIdentifier("InvalidLocation");
    }

    @Test
    void givenMaxWarehousesReachedForLocationThenThrowException() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU123");
        warehouse.setLocation("ValidLocation");
        Location location = new Location("ValidLocation", 5, 40);
        when(locationResolver.resolveByIdentifier("ValidLocation")).thenReturn(location);
        when(warehouseStore.countAllWarehousesByBuCode("BU123")).thenReturn(5);

        assertThrows(WarehouseException.class, () -> warehouseValidation.isLocationAndWarehouseFeasible(warehouse));
        verify(locationResolver, times(1)).resolveByIdentifier("ValidLocation");
        verify(warehouseStore, times(1)).countAllWarehousesByBuCode("BU123");
    }

    @Test
    void givenWarehouseCapacityExceedsLocationMaxCapacityThenThrowException() {
        Warehouse warehouse = new Warehouse();
        warehouse.setCapacity(200);
        warehouse.setLocation("ValidLocation");
        Location location = new Location("ValidLocation", 10, 100);
        when(locationResolver.resolveByIdentifier("ValidLocation")).thenReturn(location);

        assertThrows(WarehouseException.class, () -> warehouseValidation.isLocationAndWarehouseFeasible(warehouse));
        verify(locationResolver, times(1)).resolveByIdentifier("ValidLocation");
    }

    @Test
    void givenWarehouseStockExceedsCapacityThenThrowException() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU123");
        warehouse.setStock(150);
        warehouse.setCapacity(100);
        when(warehouseStore.checkWarehouseBusinessCodeExists(anyString())).thenReturn(false);
        when(locationResolver.resolveByIdentifier(anyString())).thenReturn(any());
        assertThrows(WarehouseException.class, () -> warehouseValidation.validateWarehouse(warehouse));
        verify(warehouseStore, times(1)).checkWarehouseBusinessCodeExists(anyString());
    }

    @Test
    void givenValidWarehouseThenValidationSucceeds() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU123");
        warehouse.setStock(50);
        warehouse.setCapacity(100);
        warehouse.setLocation("ValidLocation");
        Location location = new Location("ValidLocation", 10, 200);

        when(warehouseStore.checkWarehouseBusinessCodeExists("BU123")).thenReturn(false);
        when(locationResolver.resolveByIdentifier("ValidLocation")).thenReturn(location);
        when(warehouseStore.countAllWarehousesByBuCode("BU123")).thenReturn(5);

        warehouseValidation.validateWarehouse(warehouse);

        verify(warehouseStore, times(1)).checkWarehouseBusinessCodeExists("BU123");
        verify(locationResolver, times(1)).resolveByIdentifier("ValidLocation");
        verify(warehouseStore, times(1)).countAllWarehousesByBuCode("BU123");
    }

    @Test
    void givenValidWarehouseThenCheckLocationAndWarehouseFeasibilitySucceeds() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU123");
        warehouse.setLocation("ValidLocation");
        warehouse.setCapacity(50);
        Location location = new Location("ValidLocation", 10, 100);

        when(locationResolver.resolveByIdentifier("ValidLocation")).thenReturn(location);
        when(warehouseStore.countAllWarehousesByBuCode("BU123")).thenReturn(5);

        boolean isFeasible = warehouseValidation.isLocationAndWarehouseFeasible(warehouse);

        verify(locationResolver, times(1)).resolveByIdentifier("ValidLocation");
        verify(warehouseStore, times(1)).countAllWarehousesByBuCode("BU123");
        assert isFeasible;
    }
}