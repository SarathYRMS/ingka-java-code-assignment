package com.fulfilment.application.monolith.warehouses.adapters.database;

import java.time.LocalDateTime;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.mapper.WarehouseMapper;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

    @Override
    public void create(Warehouse warehouse) {
        var dbWarehouse = WarehouseMapper.getINSTANCE().toDbWarehouse(warehouse);
        persist(dbWarehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
        var dbWarehouse = WarehouseMapper.getINSTANCE().toDbWarehouse(warehouse);
        persist(dbWarehouse);
    }

    @Override
    public void remove(Warehouse warehouse) {
        update("archivedAt = ?1 where businessUnitCode = ?2", LocalDateTime.now(), warehouse.getBusinessUnitCode());
    }

    @Override
    public boolean checkWarehouseBusinessCodeExists(String buCode) {
        return find("businessUnitCode", buCode).firstResultOptional().isPresent();
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
        var dbWarehouse = find("businessUnitCode", buCode).firstResult();

      return getWarehouse(dbWarehouse);
    }

    @Override
    public Warehouse findByWarehouseId(Long id) {
        var dbWarehouse = findById(id);
        return getWarehouse(dbWarehouse);
    }

    @Override
    public Integer countAllWarehousesByBuCode(String buCode) {
        return list("businessUnitCode", buCode).stream()
                .toList()
                .size();
    }

    private static Warehouse getWarehouse(DbWarehouse dbWarehouse) {
        return WarehouseMapper.getINSTANCE().toWarehouse(dbWarehouse);
    }

}
