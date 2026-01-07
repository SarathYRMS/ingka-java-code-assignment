package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.exception.WarehouseException;
import com.fulfilment.application.monolith.warehouses.mapper.WarehouseMapper;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class WarehouseResourceImpl implements WarehouseResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseResourceImpl.class);

  private final WarehouseRepository warehouseRepository;
  private final ArchiveWarehouseUseCase archiveWarehouseUseCase;
  private final CreateWarehouseUseCase createWarehouseUseCase;
  private final ReplaceWarehouseUseCase replaceWarehouseUseCase;

    public WarehouseResourceImpl(WarehouseRepository warehouseRepository, ArchiveWarehouseUseCase archiveWarehouseUseCase,
        CreateWarehouseUseCase createWarehouseUseCase, ReplaceWarehouseUseCase replaceWarehouseUseCase) {
        this.warehouseRepository = warehouseRepository;
        this.archiveWarehouseUseCase = archiveWarehouseUseCase;
        this.createWarehouseUseCase = createWarehouseUseCase;
        this.replaceWarehouseUseCase = replaceWarehouseUseCase;
    }

    @Override
  public List<Warehouse> listAllWarehousesUnits() {
    LOGGER.info("Listing all warehouse units");
    return warehouseRepository.list("archivedAt is null").
            stream()
        .map(WarehouseMapper.getINSTANCE()::toApiWarehouseModel)
        .toList();
  }

  @Override
  @Transactional
  public Warehouse replaceAWarehouseUnit(Warehouse data) {
    LOGGER.info("Replacing a warehouse unit with data: {}", data);
    var warehouseDomainModel = getDomainWarehouse(data);

    return replaceWarehouseUseCase.replace(warehouseDomainModel);
  }

  @Override
  @Transactional
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    LOGGER.info("Creating a new warehouse unit with data: {}", data);
    var warehouseDomainModel = getDomainWarehouse(data);

    Warehouse warehouse = createWarehouseUseCase.create(warehouseDomainModel);
    return warehouse;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(long id) {
    var warehouse = warehouseRepository.findByWarehouseId(id);
    if (warehouse == null) {
      throw new WarehouseException(String.format("Warehouse with the specified ID %d does not exist.", id), 404);
    }
    return WarehouseMapper.getINSTANCE().toEntity(warehouse);
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(long id) {
    LOGGER.info("Archiving warehouse unit with ID: {}", id);
    var warehouse = warehouseRepository.findByWarehouseId(id);

    archiveWarehouseUseCase.archive(warehouse);
  }

  private static com.fulfilment.application.monolith.warehouses.domain.models.Warehouse getDomainWarehouse(Warehouse data) {
    return WarehouseMapper.getINSTANCE().toDomainWarehouse(data);
  }
}
