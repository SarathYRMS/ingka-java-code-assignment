package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.util.Constants;
import jakarta.enterprise.context.ApplicationScoped;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.exception.WarehouseException;

@ApplicationScoped
public class WarehouseValidation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public WarehouseValidation(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    public void validateWarehouse(Warehouse warehouse) {
        var isBusinessCodeExist = warehouseStore.checkWarehouseBusinessCodeExists(warehouse.getBusinessUnitCode());
        if (isBusinessCodeExist) {
            throw new WarehouseException(String.format(Constants.BUSINESS_CODE_EXISTS, warehouse.getBusinessUnitCode()), 400);
        }

        if (isLocationAndWarehouseFeasible(warehouse)) {
            if (warehouse.getStock() > warehouse.getCapacity()) {
                throw new WarehouseException(Constants.WAREHOUSE_EXCEEDS_CAPACITY, 400);
            }

        } else {
            throw new WarehouseException(Constants.VALIDATION_FAILED, 400);
        }
    }

    public Boolean isLocationAndWarehouseFeasible(Warehouse warehouse) {
        Location location = locationResolver.resolveByIdentifier(warehouse.getLocation());
        if (location == null) {
            throw new WarehouseException(Constants.INVALID_LOCATION, 400);
        }

        var countAllWarehousesByBuCode = warehouseStore.countAllWarehousesByBuCode(warehouse.getBusinessUnitCode());
        if (countAllWarehousesByBuCode >= location.maxNumberOfWarehouses) {
            throw new WarehouseException(Constants.MAX_NO_OF_WAREHOUSES_REACHED, 400);
        }

        if (warehouse.getCapacity() > location.maxCapacity) {
            throw new WarehouseException(Constants.WAREHOUSE_CAPACITY_EXCEEDS_MAX_CAPACITY, 400);
        }
        return true;
    }
}
