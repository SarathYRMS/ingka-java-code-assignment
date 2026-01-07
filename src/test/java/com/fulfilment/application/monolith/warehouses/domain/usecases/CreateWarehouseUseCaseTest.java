package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidation;
import com.fulfilment.application.monolith.warehouses.exception.WarehouseException;

public class CreateWarehouseUseCaseTest {
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
        when(warehouseValidation.validateWarehouse(warehouse)).thenReturn(null);

        assertThrows(WarehouseException.class, () -> createWarehouseUseCase.create(warehouse));
        verify(warehouseValidation, times(1)).validateWarehouse(warehouse);
        verifyNoInteractions(warehouseStore);
    }

    @Test
    void givenValidWarehouseThenCreateNewWarehouse() {
        Warehouse warehouse = new Warehouse();
        Warehouse validatedWarehouse = new Warehouse();
        when(warehouseValidation.validateWarehouse(warehouse)).thenReturn(validatedWarehouse);

        final com.warehouse.api.beans.Warehouse warehouse1 = createWarehouseUseCase.create(warehouse);
        assertNotNull(warehouse1);

        verify(warehouseValidation, times(1)).validateWarehouse(warehouse);
        verify(warehouseStore, times(1)).create(validatedWarehouse);
    }
}
