package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

    @Test
    public void createShouldPersistAndSetCreatedAt() {
        var warehouseStore = new InMemoryWarehouseStore();
        var locationResolver = resolverWith(new Location("ZWOLLE-001", 2, 100));
        var useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

        Warehouse warehouse = warehouse("MWH.100", "ZWOLLE-001", 40, 10);

        useCase.create(warehouse);

        assertNotNull(warehouse.createdAt);
        assertEquals(1, warehouseStore.getAll().size());
        assertEquals("MWH.100", warehouseStore.getAll().get(0).businessUnitCode);
    }

    @Test
    public void createShouldRejectDuplicateBusinessUnitCode() {
        var warehouseStore = new InMemoryWarehouseStore();
        warehouseStore.create(warehouse("MWH.001", "ZWOLLE-001", 20, 10));
        var locationResolver = resolverWith(new Location("ZWOLLE-001", 2, 100));
        var useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(warehouse("MWH.001", "ZWOLLE-001", 30, 10)));

        assertEquals("Business Unit Code already exists", error.getMessage());
    }

    @Test
    public void createShouldRejectInvalidLocation() {
        var warehouseStore = new InMemoryWarehouseStore();
        var locationResolver = resolverWith();
        var useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(warehouse("MWH.009", "UNKNOWN", 30, 10)));

        assertEquals("Invalid location", error.getMessage());
    }

    @Test
    public void createShouldRejectStockGreaterThanWarehouseCapacity() {
        var warehouseStore = new InMemoryWarehouseStore();
        var locationResolver = resolverWith(new Location("ZWOLLE-001", 2, 100));
        var useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(warehouse("MWH.010", "ZWOLLE-001", 20, 21)));

        assertEquals("Stock exceeds warehouse capacity", error.getMessage());
    }

    @Test
    public void createShouldRejectWhenMaxWarehousesAtLocationReached() {
        var warehouseStore = new InMemoryWarehouseStore();
        warehouseStore.create(warehouse("MWH.001", "ZWOLLE-001", 20, 10));
        var locationResolver = resolverWith(new Location("ZWOLLE-001", 1, 100));
        var useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(warehouse("MWH.011", "ZWOLLE-001", 20, 10)));

        assertEquals("Maximum number of warehouses reached", error.getMessage());
    }

    @Test
    public void createShouldRejectWhenTotalLocationCapacityWouldOverflow() {
        var warehouseStore = new InMemoryWarehouseStore();
        warehouseStore.create(warehouse("MWH.001", "ZWOLLE-001", 60, 10));
        var locationResolver = resolverWith(new Location("ZWOLLE-001", 3, 100));
        var useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.create(warehouse("MWH.012", "ZWOLLE-001", 50, 20)));

        assertEquals("Capacity exceeds location maximum", error.getMessage());
    }

    private static Warehouse warehouse(String bu, String location, int capacity, int stock) {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = bu;
        warehouse.location = location;
        warehouse.capacity = capacity;
        warehouse.stock = stock;
        return warehouse;
    }

    private static LocationResolver resolverWith(Location... locations) {
        Map<String, Location> map = new HashMap<>();
        for (Location location : locations) {
            map.put(location.identification, location);
        }
        return map::get;
    }

    private static class InMemoryWarehouseStore implements WarehouseStore {
        private final List<Warehouse> warehouses = new ArrayList<>();

        @Override
        public List<Warehouse> getAll() {
            return warehouses;
        }

        @Override
        public void create(Warehouse warehouse) {
            warehouses.add(warehouse);
        }

        @Override
        public void update(Warehouse warehouse) {
            // Not needed for create use case tests.
        }

        @Override
        public void remove(Warehouse warehouse) {
            warehouses.removeIf(w -> w.businessUnitCode.equals(warehouse.businessUnitCode));
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return warehouses.stream()
                    .filter(w -> w.businessUnitCode.equals(buCode))
                    .findFirst()
                    .orElse(null);
        }
    }
}
