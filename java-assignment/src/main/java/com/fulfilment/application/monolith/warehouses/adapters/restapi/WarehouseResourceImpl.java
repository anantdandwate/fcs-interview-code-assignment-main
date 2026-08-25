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
import jakarta.ws.rs.WebApplicationException;
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
    try {
      createWarehouseUseCase.create(toDomainWarehouse(data));
    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(e.getMessage(), 400);
    }

    return data;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {

    var warehouse = warehouseRepository.findByBusinessUnitCode(id);

    if (warehouse == null) {
      throw new WebApplicationException("Warehouse not found: " + id, 404);
    }

    return toWarehouseResponse(warehouse);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {

    var warehouse = warehouseRepository.findByBusinessUnitCode(id);

    if (warehouse == null) {
      throw new WebApplicationException("Warehouse not found: " + id, 404);
    }

    archiveWarehouseUseCase.archive(warehouse);
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode,
      @NotNull Warehouse data) {

    var domainWarehouse = toDomainWarehouse(data);

    domainWarehouse.businessUnitCode = businessUnitCode;

    try {
      replaceWarehouseUseCase.replace(domainWarehouse);
    } catch (IllegalArgumentException e) {
      if ("Warehouse not found".equals(e.getMessage())) {
        throw new WebApplicationException(e.getMessage(), 404);
      }
      throw new WebApplicationException(e.getMessage(), 400);
    }

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
