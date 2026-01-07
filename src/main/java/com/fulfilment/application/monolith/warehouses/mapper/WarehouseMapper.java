package com.fulfilment.application.monolith.warehouses.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.mapstruct.Mapper;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

@Mapper(componentModel = "cdi")
public abstract class WarehouseMapper {

    private final static WarehouseMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(WarehouseMapper.class);

    public static WarehouseMapper getINSTANCE() {
        return INSTANCE;
    }

    public abstract Warehouse toDomainWarehouse(com.warehouse.api.beans.Warehouse warehouse);

    public abstract com.warehouse.api.beans.Warehouse toEntity(Warehouse warehouse);

    public abstract Warehouse toWarehouse(DbWarehouse warehouse);

    public abstract DbWarehouse toDbWarehouse(Warehouse warehouse);

    public abstract com.warehouse.api.beans.Warehouse toApiWarehouseModel(DbWarehouse dbWarehouse);

    // Custom mapping method for LocalDateTime to ZonedDateTime
    protected ZonedDateTime mapZonedDate(LocalDateTime value) {
        return value == null ? null : value.atZone(ZoneId.systemDefault());
    }

    // Custom mapping method for ZonedDateTime to LocalDateTime
    protected LocalDateTime mapLocalDate(ZonedDateTime value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
