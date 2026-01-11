package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidation;
import com.fulfilment.application.monolith.warehouses.exception.WarehouseException;

class CreateWarehouseUseCaseTest {
    private WarehouseStore warehouseStore;
    private WarehouseValidation warehouseValidation;
    private CreateWarehouseUseCase createWarehouseUseCase;

    @BeforeEach
    void setUp() {
        warehouseStore = Mockito.mock(WarehouseStore.class);
        warehouseValidation = Mockito.mock(WarehouseValidation.class);
        createWarehouseUseCase = new CreateWarehouseUseCase(warehouseStore, warehouseValidation);
    }

    @Test
    void givenInvalidWareHouseThenThrowException() {
        Warehouse warehouse = new Warehouse();
        doNothing().when(warehouseValidation).validateWarehouse(warehouse);
        doThrow(new WarehouseException("Validation failed", 400)).when(warehouseValidation).validateWarehouse(warehouse);

        assertThrows(WarehouseException.class, () -> createWarehouseUseCase.create(warehouse));
        verify(warehouseValidation, times(1)).validateWarehouse(warehouse);
        verifyNoInteractions(warehouseStore);
    }

    @Test
    void givenValidWarehouseThenCreateNewWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU123");
        doNothing().when(warehouseValidation).validateWarehouse(warehouse);

        com.warehouse.api.beans.Warehouse warehouse1 = createWarehouseUseCase.create(warehouse);
        assertNotNull(warehouse1);

        verify(warehouseValidation, times(1)).validateWarehouse(warehouse);
        verify(warehouseStore, times(1)).create(warehouse);
    }
}
