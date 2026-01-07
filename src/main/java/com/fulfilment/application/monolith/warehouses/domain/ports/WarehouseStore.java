package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

public interface WarehouseStore {
  void create(Warehouse warehouse);

  void update(Warehouse warehouse);

  void remove(Warehouse warehouse);

  Warehouse findByBusinessUnitCode(String buCode);

  Warehouse findByWarehouseId(Long id);

  Integer countAllWarehousesByBuCode(String buCode);

  boolean checkWarehouseBusinessCodeExists(String buCode);
}
