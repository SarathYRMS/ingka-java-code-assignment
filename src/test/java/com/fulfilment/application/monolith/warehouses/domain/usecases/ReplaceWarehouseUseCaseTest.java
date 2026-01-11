package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidation;
import com.fulfilment.application.monolith.warehouses.exception.WarehouseException;

class ReplaceWarehouseUseCaseTest {
    private WarehouseStore warehouseStore;
    private WarehouseValidation warehouseValidation;
    private ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @BeforeEach
    void setUp() {
        warehouseStore = Mockito.mock(WarehouseStore.class);
        warehouseValidation = Mockito.mock(WarehouseValidation.class);
        replaceWarehouseUseCase = new ReplaceWarehouseUseCase(warehouseStore, warehouseValidation);
    }

    @Test
    void shouldThrowExceptionWhenOldWarehouseDoesNotExist() {
        Warehouse newWarehouse = new Warehouse();
        when(warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode())).thenReturn(null);

        assertThrows(WarehouseException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
        verify(warehouseStore, times(1)).findByBusinessUnitCode(newWarehouse.getBusinessUnitCode());
    }

    @Test
    void shouldThrowExceptionWhenLocationOrCapacityIsNotFeasible() {
        Warehouse newWarehouse = new Warehouse();
        Warehouse oldWarehouse = new Warehouse();
        when(warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode())).thenReturn(oldWarehouse);
        when(warehouseValidation.isLocationAndWarehouseFeasible(newWarehouse)).thenReturn(false);

        assertThrows(WarehouseException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
        verify(warehouseValidation, times(1)).isLocationAndWarehouseFeasible(newWarehouse);
    }

    @Test
    void shouldThrowExceptionWhenStockDoesNotMatch() {
        Warehouse newWarehouse = new Warehouse();
        Warehouse oldWarehouse = new Warehouse();
        oldWarehouse.setStock(100);
        newWarehouse.setStock(200);
        when(warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode())).thenReturn(oldWarehouse);
        when(warehouseValidation.isLocationAndWarehouseFeasible(newWarehouse)).thenReturn(true);

        assertThrows(WarehouseException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }

    @Test
    void shouldThrowExceptionWhenCapacityIsInsufficient() {
        Warehouse newWarehouse = new Warehouse();
        Warehouse oldWarehouse = new Warehouse();
        oldWarehouse.setStock(100);
        newWarehouse.setStock(100);
        newWarehouse.setCapacity(50);
        when(warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode())).thenReturn(oldWarehouse);
        when(warehouseValidation.isLocationAndWarehouseFeasible(newWarehouse)).thenReturn(true);

        assertThrows(WarehouseException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }

    @Test
    void shouldReplaceWarehouseSuccessfully() {
        Warehouse newWarehouse = new Warehouse();
        Warehouse oldWarehouse = new Warehouse();
        oldWarehouse.setStock(100);
        newWarehouse.setStock(100);
        newWarehouse.setCapacity(200);
        when(warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode())).thenReturn(oldWarehouse);
        when(warehouseValidation.isLocationAndWarehouseFeasible(newWarehouse)).thenReturn(true);

        replaceWarehouseUseCase.replace(newWarehouse);

        verify(warehouseStore, times(1)).update(newWarehouse);
        verify(warehouseValidation, times(1)).isLocationAndWarehouseFeasible(newWarehouse);
    }
}
