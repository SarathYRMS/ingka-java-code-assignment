package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.exception.WarehouseException;

class ArchiveWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @BeforeEach
    void setUp() {
        warehouseStore = Mockito.mock(WarehouseStore.class);
        archiveWarehouseUseCase = new ArchiveWarehouseUseCase(warehouseStore);
    }

    @Test
    void shouldThrowExceptionWhenWarehouseIsNull() {
        assertThrows(WarehouseException.class, () -> archiveWarehouseUseCase.archive(null));
    }

    @Test
    void shouldCallRemoveWhenWarehouseIsNotNull() {
        Warehouse warehouse = new Warehouse(); // Assuming a no-args constructor exists
        archiveWarehouseUseCase.archive(warehouse);
        verify(warehouseStore, times(1)).remove(warehouse);
    }
}
