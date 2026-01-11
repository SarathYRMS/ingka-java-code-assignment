package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidation;
import com.fulfilment.application.monolith.warehouses.mapper.WarehouseMapper;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateWarehouseUseCase.class);

    private final WarehouseStore warehouseStore;
    private final WarehouseValidation warehouseValidation;

    public CreateWarehouseUseCase(WarehouseStore warehouseStore, WarehouseValidation warehouseValidation) {
        this.warehouseStore = warehouseStore;
        this.warehouseValidation = warehouseValidation;
    }

    @Override
    public com.warehouse.api.beans.Warehouse create(Warehouse warehouse) {
        LOGGER.info("Creating warehouse with business unit code: {}", warehouse.getBusinessUnitCode());
        warehouseValidation.validateWarehouse(warehouse);

        warehouseStore.create(warehouse);
        return WarehouseMapper.getINSTANCE().toEntity(warehouse);
    }

}
