package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    Warehouse currentWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);

    if (currentWarehouse == null) {
      throw new IllegalArgumentException("Warehouse not found");
    }

    Location location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Invalid location");
    }

    if (newWarehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException("Capacity exceeds location maximum");
    }

    if (newWarehouse.capacity < currentWarehouse.stock) {
      throw new IllegalArgumentException("New warehouse capacity cannot accommodate current stock");
    }

    if (!newWarehouse.stock.equals(currentWarehouse.stock)) {
      throw new IllegalArgumentException("Stock must match the warehouse being replaced");
    }

    if (newWarehouse.stock > newWarehouse.capacity) {
      throw new IllegalArgumentException("Stock exceeds warehouse capacity");
    }

    long activeWarehousesAtLocation = warehouseStore.getAll().stream()
        .filter(w -> w.location.equals(newWarehouse.location)).count();
    boolean changesLocation = !currentWarehouse.location.equals(newWarehouse.location);

    if (changesLocation && activeWarehousesAtLocation >= location.maxNumberOfWarehouses) {
      throw new IllegalArgumentException("Maximum number of warehouses reached");
    }

    int activeCapacityAtLocation = warehouseStore.getAll().stream()
        .filter(w -> w.location.equals(newWarehouse.location))
        .mapToInt(w -> w.capacity)
        .sum();
    int capacityWithoutReplacedWarehouse = changesLocation ? activeCapacityAtLocation
        : (activeCapacityAtLocation - currentWarehouse.capacity);

    if (capacityWithoutReplacedWarehouse + newWarehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException("Capacity exceeds location maximum");
    }

    currentWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(currentWarehouse);

    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }
}
