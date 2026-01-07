package com.fulfilment.application.monolith.warehouses.domain.validation;

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

    public Warehouse validateWarehouse(Warehouse warehouse) {
        var isBusinessCodeExist = warehouseStore.checkWarehouseBusinessCodeExists(warehouse.getBusinessUnitCode());
        if (isBusinessCodeExist) {
            throw new WarehouseException("A warehouse with the specified business unit code already exists.", 400);
        }

        if (checkLocationAndWarehouseFeasibility(warehouse)) {
            if (warehouse.getStock() > warehouse.getCapacity()) {
                throw new WarehouseException("The warehouse stock cannot exceed its capacity.", 400);
            }
            return warehouse;
        }
        return null;
    }

    public Boolean checkLocationAndWarehouseFeasibility(Warehouse warehouse) {
        Location location = locationResolver.resolveByIdentifier(warehouse.getLocation());
        if (location == null) {
            throw new WarehouseException("The specified location is invalid.", 400);
        }

        var countAllWarehousesByBuCode = warehouseStore.countAllWarehousesByBuCode(warehouse.getBusinessUnitCode());
        if ( countAllWarehousesByBuCode >= location.maxNumberOfWarehouses) {
            throw new WarehouseException("The maximum number of warehouses for this location has been reached.", 400);
        }

        if (warehouse.getCapacity() > location.maxCapacity) {
            throw new WarehouseException("The warehouse capacity exceeds the maximum capacity for this location.", 400);
        }
        return true;
    }
}
