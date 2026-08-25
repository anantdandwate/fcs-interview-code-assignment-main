package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.time.LocalDateTime;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new IllegalArgumentException("Business Unit Code already exists");
    }
    Location location = locationResolver.resolveByIdentifier(warehouse.location);

    if (location == null) {
      throw new IllegalArgumentException("Invalid location");
    }

    if (warehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException("Capacity exceeds location maximum");
    }

    if (warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException("Stock exceeds warehouse capacity");
    }

    long warehouseAtLocation = warehouseStore.getAll()
        .stream().filter(w -> w.location.equals(warehouse.location)).count();

    if (warehouseAtLocation >= location.maxNumberOfWarehouses) {
      throw new IllegalArgumentException("Maximum number of warehouses reached");
    }

    int totalActiveCapacityAtLocation = warehouseStore.getAll().stream()
        .filter(w -> w.location.equals(warehouse.location))
        .mapToInt(w -> w.capacity)
        .sum();

    if (totalActiveCapacityAtLocation + warehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException("Capacity exceeds location maximum");
    }

    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;
    warehouseStore.create(warehouse);
  }
}
