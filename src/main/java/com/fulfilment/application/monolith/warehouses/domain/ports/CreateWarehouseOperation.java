package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

public interface CreateWarehouseOperation {
    com.warehouse.api.beans.Warehouse create(Warehouse warehouse);
}
