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

public class ReplaceWarehouseUseCaseTest {

    @Test
    public void replaceShouldArchiveCurrentAndCreateNewWarehouse() {
        var warehouseStore = new InMemoryWarehouseStore();
        warehouseStore.create(activeWarehouse("MWH.001", "ZWOLLE-001", 80, 30));
        var locationResolver = resolverWith(new Location("ZWOLLE-001", 2, 150));
        var useCase = new ReplaceWarehouseUseCase(warehouseStore, locationResolver);

        Warehouse replacement = activeWarehouse("MWH.001", "ZWOLLE-001", 90, 30);

        useCase.replace(replacement);

        assertEquals(2, warehouseStore.allWarehouses().size());
        assertNotNull(warehouseStore.allWarehouses().get(0).archivedAt);
        assertNotNull(warehouseStore.allWarehouses().get(1).createdAt);
        assertEquals("ZWOLLE-001", warehouseStore.allWarehouses().get(1).location);
    }

    @Test
    public void replaceShouldRejectWhenCurrentWarehouseDoesNotExist() {
        var useCase = new ReplaceWarehouseUseCase(new InMemoryWarehouseStore(), resolverWith());

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.replace(activeWarehouse("MWH.999", "ZWOLLE-001", 50, 20)));

        assertEquals("Warehouse not found", error.getMessage());
    }

    @Test
    public void replaceShouldRejectWhenStockDoesNotMatchCurrentWarehouse() {
        var warehouseStore = new InMemoryWarehouseStore();
        warehouseStore.create(activeWarehouse("MWH.001", "ZWOLLE-001", 80, 30));
        var useCase = new ReplaceWarehouseUseCase(warehouseStore, resolverWith(new Location("ZWOLLE-001", 2, 150)));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.replace(activeWarehouse("MWH.001", "ZWOLLE-001", 90, 20)));

        assertEquals("Stock must match the warehouse being replaced", error.getMessage());
    }

    @Test
    public void replaceShouldRejectWhenNewCapacityCannotAccommodateCurrentStock() {
        var warehouseStore = new InMemoryWarehouseStore();
        warehouseStore.create(activeWarehouse("MWH.001", "ZWOLLE-001", 80, 30));
        var useCase = new ReplaceWarehouseUseCase(warehouseStore, resolverWith(new Location("ZWOLLE-001", 2, 150)));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.replace(activeWarehouse("MWH.001", "ZWOLLE-001", 20, 30)));

        assertEquals("New warehouse capacity cannot accommodate current stock", error.getMessage());
    }

    private static Warehouse activeWarehouse(String bu, String location, int capacity, int stock) {
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
            return warehouses.stream().filter(w -> w.archivedAt == null).toList();
        }

        public List<Warehouse> allWarehouses() {
            return warehouses;
        }

        @Override
        public void create(Warehouse warehouse) {
            warehouses.add(warehouse);
        }

        @Override
        public void update(Warehouse warehouse) {
            for (Warehouse current : warehouses) {
                if (current.businessUnitCode.equals(warehouse.businessUnitCode) && current.archivedAt == null) {
                    current.location = warehouse.location;
                    current.capacity = warehouse.capacity;
                    current.stock = warehouse.stock;
                    current.archivedAt = warehouse.archivedAt;
                    return;
                }
            }
        }

        @Override
        public void remove(Warehouse warehouse) {
            warehouses.remove(warehouse);
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return warehouses.stream()
                    .filter(w -> w.businessUnitCode.equals(buCode) && w.archivedAt == null)
                    .findFirst()
                    .orElse(null);
        }
    }
}
