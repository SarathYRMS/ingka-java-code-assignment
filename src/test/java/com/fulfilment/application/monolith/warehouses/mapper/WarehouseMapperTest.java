package com.fulfilment.application.monolith.warehouses.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;


class WarehouseMapperTest {

    private final WarehouseMapper warehouseMapper = WarehouseMapper.getINSTANCE();

    @Test
    void testToDomainWarehouse() {
        com.warehouse.api.beans.Warehouse apiWarehouse = new com.warehouse.api.beans.Warehouse();
        apiWarehouse.setBusinessUnitCode("BU123");

        Warehouse warehouse = warehouseMapper.toDomainWarehouse(apiWarehouse);

        assertNotNull(warehouse);
        assertEquals(apiWarehouse.getId(), warehouse.getId());
        assertEquals(apiWarehouse.getBusinessUnitCode(), warehouse.getBusinessUnitCode());
    }

    @Test
    void testToEntity() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU123");

        com.warehouse.api.beans.Warehouse apiWarehouse = warehouseMapper.toEntity(warehouse);

        assertNotNull(apiWarehouse);
        assertEquals(warehouse.getId(), apiWarehouse.getId());
        assertEquals(warehouse.getBusinessUnitCode(), apiWarehouse.getBusinessUnitCode());
    }

    @Test
    void testToWarehouse() {
        DbWarehouse dbWarehouse = new DbWarehouse();
        dbWarehouse.businessUnitCode = "BU123";

        Warehouse warehouse = warehouseMapper.toWarehouse(dbWarehouse);

        assertNotNull(warehouse);
        assertEquals(dbWarehouse.id, warehouse.getId());
        assertEquals(dbWarehouse.businessUnitCode, warehouse.getBusinessUnitCode());
    }

    @Test
    void testToDbWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU123");

        DbWarehouse dbWarehouse = warehouseMapper.toDbWarehouse(warehouse);

        assertNotNull(dbWarehouse);
        assertEquals(warehouse.getId(), dbWarehouse.id);
        assertEquals(warehouse.getBusinessUnitCode(), dbWarehouse.businessUnitCode);
    }

    @Test
    void testMapZonedDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = warehouseMapper.mapZonedDate(localDateTime);

        assertNotNull(zonedDateTime);
        assertEquals(localDateTime, zonedDateTime.toLocalDateTime());
    }

    @Test
    void testMapLocalDate() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        LocalDateTime localDateTime = warehouseMapper.mapLocalDate(zonedDateTime);

        assertNotNull(localDateTime);
        assertEquals(zonedDateTime.toLocalDateTime(), localDateTime);
    }
}