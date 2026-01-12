package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.util.Constants;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.exception.WarehouseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WarehouseResourceImplTest {
    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @Mock
    private CreateWarehouseUseCase createWarehouseUseCase;

    @Mock
    private ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @InjectMocks
    private WarehouseResourceImpl warehouseResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAllWarehousesUnits() {
        // given
        var dbWarehouse = new com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse();
        dbWarehouse.businessUnitCode = "BU001";
        when(warehouseRepository.list("archivedAt is null")).thenReturn(List.of(dbWarehouse));

        // when
        List<com.warehouse.api.beans.Warehouse> result = warehouseResource.listAllWarehousesUnits();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BU001", result.get(0).getBusinessUnitCode());
        verify(warehouseRepository).list("archivedAt is null");
    }

    @Test
    void testReplaceAWarehouseUnit() {
        // given
        com.warehouse.api.beans.Warehouse apiWarehouse = new com.warehouse.api.beans.Warehouse();
        apiWarehouse.setBusinessUnitCode("BU002");
        Warehouse domainWarehouse = new Warehouse();
        domainWarehouse.setBusinessUnitCode("BU002");
        when(replaceWarehouseUseCase.replace(domainWarehouse)).thenReturn(apiWarehouse);

        // when
        com.warehouse.api.beans.Warehouse result = warehouseResource.replaceAWarehouseUnit(apiWarehouse);

        // then
        assertNotNull(result);
        verify(replaceWarehouseUseCase).replace(domainWarehouse);
    }

    @Test
    void testGetAWarehouseUnitByID_Success() {
        // given
        long id = 1L;
        var dbWarehouse = mock(com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse.class);
        var domainWarehouse = new Warehouse();
        domainWarehouse.setId(id);
        when(warehouseRepository.findByWarehouseId(id)).thenReturn(domainWarehouse);

        // when
        com.warehouse.api.beans.Warehouse result = warehouseResource.getAWarehouseUnitByID(id);

        // then
        assertNotNull(result);
        assertNotNull(result.getId());
        verify(warehouseRepository).findByWarehouseId(id);
    }

    @Test
    void testCreateANewWarehouseUnit() {
        // given
        com.warehouse.api.beans.Warehouse apiWarehouse = new com.warehouse.api.beans.Warehouse();
        apiWarehouse.setBusinessUnitCode("BU002");

        Warehouse domainWarehouse = new Warehouse();
        domainWarehouse.setBusinessUnitCode("BU002");
        when(createWarehouseUseCase.create(domainWarehouse)).thenReturn(apiWarehouse);

        // when
        com.warehouse.api.beans.Warehouse result = warehouseResource.createANewWarehouseUnit(apiWarehouse);

        // then
        assertNotNull(result);
        verify(createWarehouseUseCase).create(domainWarehouse);
    }

    @Test
    void testGetAWarehouseUnitByID_NotFound() {
        // given
        long id = 1L;
        when(warehouseRepository.findByWarehouseId(id)).thenReturn(null);

        // when & then
        WarehouseException exception = assertThrows(WarehouseException.class, () -> warehouseResource.getAWarehouseUnitByID(id));
        assertEquals(String.format(Constants.ID_NOT_FOUNT, id), exception.getMessage());
    }

    @Test
    void testArchiveAWarehouseUnitByID() {
        // given
        long id = 1L;
        var domainWarehouse = new Warehouse();
        domainWarehouse.setId(id);
        when(warehouseRepository.findByWarehouseId(id)).thenReturn(domainWarehouse);

        // when
        warehouseResource.archiveAWarehouseUnitByID(id);

        // then
        verify(archiveWarehouseUseCase, times(1)).archive(domainWarehouse);
    }
}
