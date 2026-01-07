package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.util.Objects;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidation;
import com.fulfilment.application.monolith.warehouses.exception.WarehouseException;
import com.fulfilment.application.monolith.warehouses.mapper.WarehouseMapper;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReplaceWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;
  private final WarehouseValidation warehouseValidation;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, WarehouseValidation warehouseValidation) {
      this.warehouseStore = warehouseStore;
      this.warehouseValidation = warehouseValidation;
  }

  @Override
  public com.warehouse.api.beans.Warehouse replace(Warehouse newWarehouse) {
    LOGGER.info("Replacing warehouse with business unit code: {}", newWarehouse.getBusinessUnitCode());
    var oldWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode());
    if (oldWarehouse == null) {
      throw new WarehouseException("Warehouse with the specified business unit code does not exist to replace.", 404);
    }

    if (!warehouseValidation.checkLocationAndWarehouseFeasibility(newWarehouse)) {
        throw new WarehouseException("New warehouse location or capacity is not feasible for replacement.", 400);
    }

    if (!Objects.equals(newWarehouse.getStock(), oldWarehouse.getStock())) {
      throw new WarehouseException("The stock of the new warehouse must match the stock of the warehouse being replaced.", 400);
    }

    if (newWarehouse.getCapacity() < oldWarehouse.getStock()) {
      throw new WarehouseException("The new warehouse's capacity cannot accommodate the stock of the warehouse being replaced.", 400);
    }

    warehouseStore.update(newWarehouse);
    return WarehouseMapper.getINSTANCE().toEntity(newWarehouse);
  }
}
