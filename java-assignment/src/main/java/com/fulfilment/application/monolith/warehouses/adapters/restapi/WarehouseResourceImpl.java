package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject
  private WarehouseRepository warehouseRepository;

  @Inject
  CreateWarehouseUseCase createWarehouseUseCase;

  @Inject
  ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @Inject
  ArchiveWarehouseUseCase archiveWarehouseUseCase;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {

    createWarehouseUseCase.create(
        toDomainWarehouse(data));

    return data;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {

    var warehouse = warehouseRepository.findByBusinessUnitCode(id);

    if (warehouse == null) {
      throw new RuntimeException(
          "Warehouse not found: " + id);
    }

    return toWarehouseResponse(warehouse);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {

    var warehouse = warehouseRepository.findByBusinessUnitCode(id);

    if (warehouse == null) {
      throw new RuntimeException(
          "Warehouse not found: " + id);
    }

    archiveWarehouseUseCase.archive(warehouse);
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode,
      @NotNull Warehouse data) {

    var domainWarehouse = toDomainWarehouse(data);

    domainWarehouse.businessUnitCode = businessUnitCode;

    replaceWarehouseUseCase.replace(domainWarehouse);

    return data;
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomainWarehouse(
      Warehouse warehouse) {

    var domainWarehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();

    domainWarehouse.businessUnitCode = warehouse.getBusinessUnitCode();
    domainWarehouse.location = warehouse.getLocation();
    domainWarehouse.capacity = warehouse.getCapacity();
    domainWarehouse.stock = warehouse.getStock();

    return domainWarehouse;
  }
}
